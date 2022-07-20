package com.itrjp.demo.websocket;

import com.itrjp.demo.proto.PacketProtobuf;

/**
 * TODO
 *
 * @author <a href="mailto:r979668507@gmail.com">renjp</a>
 * @date 2022/7/18 20:01
 */
public interface WebSocketClient {
    HandshakeData getHandshake();

    /**
     * 发送消息
     *
     * @param channelId
     * @param data
     */
    void sendMessage(String channelId, PacketProtobuf.Data data);

    /**
     * 获取sessionID
     *
     * @return sessionID
     */
    String getSessionID();

    /**
     * 加入频道
     */
    void join(String channelId);

    /**
     * 离开频道
     */
    void leave(String channelId);

}
