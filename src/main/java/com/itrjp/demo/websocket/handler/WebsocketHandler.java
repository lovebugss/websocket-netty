package com.itrjp.demo.websocket.handler;

import com.itrjp.demo.proto.PacketProtobuf;
import com.itrjp.demo.websocket.ProtobufToWebSocketFrameEncode;
import com.itrjp.demo.websocket.WebSocketFrameToProtobufDecoder;
import com.itrjp.demo.websocket.WebSocketProperties;
import com.itrjp.demo.websocket.listener.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.itrjp.demo.websocket.WebSocketServerInitializer.AUTHORIZE_HANDLER;

/**
 * TODO
 *
 * @author <a href="mailto:r979668507@gmail.com">renjp</a>
 * @date 2022/7/11 17:37
 */
@Component
@ChannelHandler.Sharable
public class WebsocketHandler extends SimpleChannelInboundHandler<Object> {
    private final Logger logger = LoggerFactory.getLogger(WebsocketHandler.class);

    private final WebSocketProperties webSocketProperties;
    private final OpenListener openListener;
    private final MessageHandler messageHandler;
    private final PingListener pingListener;
    private final PongListener pongListener;
    private final ExceptionListener exceptionListener;
    private final CloseListener closeListener;

    public WebsocketHandler(WebSocketProperties webSocketProperties, OpenListener openListener,
                            MessageHandler messageHandler, PingListener pingListener, PongListener pongListener,
                            ExceptionListener exceptionListener, CloseListener closeListener) {
        this.webSocketProperties = webSocketProperties;
        this.openListener = openListener;
        this.messageHandler = messageHandler;
        this.pingListener = pingListener;
        this.pongListener = pongListener;
        this.exceptionListener = exceptionListener;
        this.closeListener = closeListener;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("WebsocketHandler#channelRead, channel: [{}], message class: [{}]", ctx.channel().id(), msg.getClass());
        if (msg instanceof CloseWebSocketFrame) {
            logger.info("WebsocketHandler#channelRead CloseWebSocketFrame");
            ctx.channel().writeAndFlush(msg).addListener(ChannelFutureListener.CLOSE);
        } else if (msg instanceof FullHttpRequest req) {
            handleHttpRequest(ctx, req);
        }
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
        logger.info("WebsocketHandler#handleHttpRequest");
        // Handshake
        handshake(ctx, req, ctx.channel());
    }

    private void handshake(ChannelHandlerContext ctx, FullHttpRequest req, Channel channel) {
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(getWebSocketLocation(req), null, true, this.webSocketProperties.getMaxFramePayloadLength());
        final WebSocketServerHandshaker handshake = wsFactory.newHandshaker(req);
        if (handshake == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(channel);
        } else {
            ChannelPipeline pipeline = ctx.pipeline();
            addWebSocketHandlers(ctx, pipeline);
            handshake.handshake(channel, req).addListener(future -> {
                if (future.isSuccess()) {
                    logger.info("handshake success");
                    // 握手成功
                    connectClient(channel);
                } else {
                    logger.info("handshake failed");
                    handshake.close(channel, new CloseWebSocketFrame());
                }
            });
        }
    }

    private void addWebSocketHandlers(ChannelHandlerContext ctx, ChannelPipeline pipeline) {
        pipeline.remove(ctx.name());
        pipeline.remove(AUTHORIZE_HANDLER);
        pipeline.addLast(new ChunkedWriteHandler());

        pipeline.addLast(new WebSocketServerCompressionHandler());
        pipeline.addLast(new WebSocketFrameAggregator(Integer.MAX_VALUE));

        pipeline.addLast(new ProtobufVarint32FrameDecoder());
        pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());

        //将WebSocketFrame转为ByteBuf 以便后面的 ProtobufDecoder 解码
        pipeline.addLast(new MessageToMessageDecoder<WebSocketFrame>(){
            @Override
            protected void decode(ChannelHandlerContext ctx, WebSocketFrame msg, List<Object> out) throws Exception {
                ByteBuf byteBuf = msg.content();
                if (msg instanceof BinaryWebSocketFrame) {
                    // 二进制消息
                    byteBuf.retain();
                    out.add(msg.content());
                } else if (msg instanceof TextWebSocketFrame text) {
                    // 二进制消息
                    byteBuf.retain();
                    out.add(text.content());
                }else if (msg instanceof PingWebSocketFrame) {
                    pingListener.onPing(ctx.channel());
                } else if (msg instanceof PongWebSocketFrame) {
                    pongListener.onPong(ctx.channel());
                }
            }
        });

        pipeline.addLast(new ProtobufDecoder(PacketProtobuf.Data.getDefaultInstance()));
        //自定义入站处理
        pipeline.addLast(messageHandler);
        //出站处理 将protoBuf实例转为WebSocketFrame
        pipeline.addLast(new ProtobufToWebSocketFrameEncode());
    }

    private void connectClient(Channel channel) {
        openListener.onOpen(channel);
    }

    private String getWebSocketLocation(FullHttpRequest req) {
        String location = req.headers().get(HttpHeaderNames.HOST) + webSocketProperties.getWebsocketPath();
        if (webSocketProperties.getSsl().isEnable()) {
            return "wss://" + location;
        } else {
            return "ws://" + location;
        }
    }
}
