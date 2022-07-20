package com.itrjp.demo.websocket.handler;

import com.itrjp.demo.proto.PacketProtobuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO
 *
 * @author <a href="mailto:r979668507@gmail.com">renjp</a>
 * @date 2022/7/11 17:58
 */

@ChannelHandler.Sharable
public class MessageHandler extends SimpleChannelInboundHandler<PacketProtobuf.Data> {
    private Logger logger = LoggerFactory.getLogger(MessageHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, PacketProtobuf.Data msg) throws Exception {

        logger.info("MessageHandler#channelRead0 message: {}, channel: [{}]", msg, ctx.channel().id());
    }
}
