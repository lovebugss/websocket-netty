package com.itrjp.demo.websocket.listener;

import io.netty.channel.Channel;

/**
 * TODO
 *
 * @author <a href="mailto:r979668507@gmail.com">renjp</a>
 * @date 2022/7/21 13:45
 */
public interface ExceptionListener {
    void onException(Channel channel, Throwable throwable);
}
