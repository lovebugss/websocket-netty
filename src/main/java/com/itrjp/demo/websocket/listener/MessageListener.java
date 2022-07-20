package com.itrjp.demo.websocket.listener;

import com.itrjp.demo.proto.PacketProtobuf;
import com.itrjp.demo.websocket.WebSocketClient;

/**
 * TODO
 *
 * @author <a href="mailto:r979668507@gmail.com">renjp</a>
 * @date 2022/7/20 18:59
 */
public interface MessageListener {

    void onMessage(WebSocketClient webSocketClient, PacketProtobuf.Data protobuf);
}
