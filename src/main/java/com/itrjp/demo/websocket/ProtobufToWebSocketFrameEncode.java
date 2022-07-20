package com.itrjp.demo.websocket;

import com.google.protobuf.MessageLiteOrBuilder;
import com.itrjp.demo.proto.PacketProtobuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

import java.util.List;

/**
 * TODO
 *
 * @author <a href="mailto:r979668507@gmail.com">renjp</a>
 * @date 2022/7/11 19:37
 */
public class ProtobufToWebSocketFrameEncode extends ProtobufEncoder {

    @Override
    protected void encode(ChannelHandlerContext ctx, MessageLiteOrBuilder msg, List<Object> out) throws Exception {
        PacketProtobuf.Data mpMsg = (PacketProtobuf.Data) msg;
        WebSocketFrame frame = new BinaryWebSocketFrame(Unpooled.wrappedBuffer(mpMsg.toByteArray()));
        out.add(frame);
    }

}
