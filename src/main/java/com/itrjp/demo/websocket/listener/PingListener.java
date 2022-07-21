package com.itrjp.demo.websocket.listener;

import io.netty.channel.Channel;

/**
 * TODO
 *
 * @author <a href="mailto:r979668507@gmail.com">renjp</a>
 * @date 2022/7/20 19:01
 */
public interface PingListener {
    void onPing(Channel channel);
}
