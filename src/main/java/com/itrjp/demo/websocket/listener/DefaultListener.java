package com.itrjp.demo.websocket.listener;

import com.itrjp.demo.proto.PacketProtobuf;
import com.itrjp.demo.websocket.WebSocketClient;

/**
 * TODO
 *
 * @author <a href="mailto:r979668507@gmail.com">renjp</a>
 * @date 2022/7/20 19:02
 */
public class DefaultListener implements MessageListener, PingListener, PongListener, CloseListener, OpenListener {

    @Override
    public void onClose(WebSocketClient webSocketClient) {

    }


    @Override
    public void onOpen(WebSocketClient webSocketClient) {

    }

    @Override
    public void onPing(WebSocketClient webSocketClient) {

    }

    @Override
    public void onPong(WebSocketClient webSocketClient) {

    }

    @Override
    public void onMessage(WebSocketClient webSocketClient, PacketProtobuf.Data protobuf) {

    }
}
