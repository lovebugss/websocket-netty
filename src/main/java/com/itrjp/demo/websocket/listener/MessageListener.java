package com.itrjp.demo.websocket.listener;

import com.itrjp.demo.proto.PacketProtobuf;
import io.netty.channel.Channel;

/**
 * TODO
 *
 * @author <a href="mailto:r979668507@gmail.com">renjp</a>
 * @date 2022/7/20 18:59
 */
public interface MessageListener {

    void onMessage(Channel channel, PacketProtobuf.Data protobuf);
}
