package com.itrjp.demo.websocket;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * WebSocketFrameToProtobufDecoder
 *
 * @author renjp
 * @date 2022/5/10 09:41
 */
public class WebSocketFrameToProtobufDecoder extends MessageToMessageDecoder<WebSocketFrame> {
    private final Logger log = LoggerFactory.getLogger(WebSocketFrameToProtobufDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, WebSocketFrame msg, List<Object> out) throws Exception {
        log.info("method: [decode], channelId: {}, class: {}", ctx.channel().id().asLongText(), msg.getClass());

        ByteBuf byteBuf = msg.content();
        if (msg instanceof BinaryWebSocketFrame || msg instanceof TextWebSocketFrame) {
            // 二进制消息
            byteBuf.retain();
            out.add(msg.content());
        }
    }
}
