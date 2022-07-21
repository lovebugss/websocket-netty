package com.itrjp.demo.websocket.listener;

import io.netty.channel.Channel;

/**
 * TODO
 *
 * @author <a href="mailto:r979668507@gmail.com">renjp</a>
 * @date 2022/7/20 19:00
 */
public interface CloseListener {
    void onClose(Channel webSocketClient);
}
