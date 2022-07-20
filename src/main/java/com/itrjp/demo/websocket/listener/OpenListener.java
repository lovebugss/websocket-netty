package com.itrjp.demo.websocket.listener;

import com.itrjp.demo.websocket.WebSocketClient;

/**
 * TODO
 *
 * @author <a href="mailto:r979668507@gmail.com">renjp</a>
 * @date 2022/7/20 19:03
 */
public interface OpenListener {
    void onOpen(WebSocketClient webSocketClient);
}
