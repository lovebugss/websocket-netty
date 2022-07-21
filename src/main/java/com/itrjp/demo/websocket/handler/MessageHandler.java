package com.itrjp.demo.websocket.handler;

import com.itrjp.demo.proto.PacketProtobuf;
import com.itrjp.demo.websocket.listener.CloseListener;
import com.itrjp.demo.websocket.listener.ExceptionListener;
import com.itrjp.demo.websocket.listener.MessageListener;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * TODO
 *
 * @author <a href="mailto:r979668507@gmail.com">renjp</a>
 * @date 2022/7/11 17:58
 */
@Component
@ChannelHandler.Sharable
public class MessageHandler extends SimpleChannelInboundHandler<PacketProtobuf.Data> {
    private final Logger logger = LoggerFactory.getLogger(MessageHandler.class);
    private final MessageListener messageListener;
    private final ExceptionListener exceptionListener;
    private final CloseListener closeListener;

    public MessageHandler(MessageListener messageListener, ExceptionListener exceptionListener, CloseListener closeListener) {
        this.messageListener = messageListener;
        this.exceptionListener = exceptionListener;
        this.closeListener = closeListener;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, PacketProtobuf.Data msg) throws Exception {
        logger.info("MessageHandler#channelRead0 message: {}, channel: [{}]", msg, ctx.channel().id());
        Channel channel = ctx.channel();
        channel.writeAndFlush(PacketProtobuf.Data.newBuilder()
                .setTimestamp(System.currentTimeMillis())
                .setContent("你好!!!")
                .setType(PacketProtobuf.DataType.message)
                .build());
        messageListener.onMessage(null, msg);
    }

    /**
     * 异常
     *
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("exceptionCaught", cause);
        exceptionListener.onException(null, cause);
        ctx.close();
    }

    //断开长链接
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        closeListener.onClose(null);
        super.channelInactive(ctx);
    }

}
