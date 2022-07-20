package com.itrjp.demo.websocket.handler;

import com.google.protobuf.MessageLiteOrBuilder;
import com.itrjp.demo.proto.PacketProtobuf;
import com.itrjp.demo.websocket.Configuration;
import com.itrjp.demo.websocket.ProtobufToWebSocketFrameEncode;
import com.itrjp.demo.websocket.listener.OpenListener;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
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
public class WebsocketHandler extends ChannelInboundHandlerAdapter {
    private final Logger logger = LoggerFactory.getLogger(WebsocketHandler.class);

    private static final String WEBSOCKET_PATH = "/ws";
    private final Configuration configuration;
    private final OpenListener openListener;

    public WebsocketHandler(Configuration configuration, OpenListener openListener) {
        this.configuration = configuration;
        this.openListener = openListener;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("WebsocketHandler#channelRead, channel: [{}], message class: [{}]", ctx.channel().id(), msg.getClass());
        if (msg instanceof CloseWebSocketFrame) {
            logger.info("WebsocketHandler#channelRead CloseWebSocketFrame");
            ctx.channel().writeAndFlush(msg).addListener(ChannelFutureListener.CLOSE);
        } else if (msg instanceof BinaryWebSocketFrame
                || msg instanceof TextWebSocketFrame) {
            logger.info("WebsocketHandler#channelRead TextWebSocketFrame");
//            ctx.pipeline().fireChannelRead(msg);
            ctx.fireChannelRead(msg);
        } else if (msg instanceof FullHttpRequest req) {
            try {
                // 升级为websocket 协议
                handleHttpRequest(ctx, req);
            } finally {
//                req.release();
            }
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
        logger.info("WebsocketHandler#handleHttpRequest");
        // Handshake
        handshake(ctx, req, ctx.channel());
    }

    private void handshake(ChannelHandlerContext ctx, FullHttpRequest req, Channel channel) {
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(getWebSocketLocation(req), null, true, this.configuration.getMaxFramePayloadLength());
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
        pipeline.addLast(new ProtobufToWebSocketFrameEncode());

        pipeline.addLast(new ProtobufDecoder(PacketProtobuf.Data.getDefaultInstance()));
        //自定义入站处理
        pipeline.addLast(new MessageHandler());
        //出站处理 将protoBuf实例转为WebSocketFrame
        pipeline.addLast(new ProtobufEncoder() {
            @Override
            protected void encode(ChannelHandlerContext ctx, MessageLiteOrBuilder msg, List<Object> out) throws Exception {
                PacketProtobuf.Data mpMsg = (PacketProtobuf.Data) msg;
                WebSocketFrame frame = new BinaryWebSocketFrame(Unpooled.wrappedBuffer(mpMsg.toByteArray()));
                out.add(frame);
            }
        });
    }

    private void connectClient(Channel channel) {
        openListener.onOpen(null);
    }

    private String getWebSocketLocation(FullHttpRequest req) {
        String location = req.headers().get(HttpHeaderNames.HOST) + WEBSOCKET_PATH;
        if (configuration.getSsl().isEnable()) {
            return "wss://" + location;
        } else {
            return "ws://" + location;
        }
    }
}
