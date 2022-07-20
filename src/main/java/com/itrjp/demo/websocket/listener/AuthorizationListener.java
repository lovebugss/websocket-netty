package com.itrjp.demo.websocket.listener;

import com.itrjp.demo.websocket.HandshakeData;

/**
 * TODO
 *
 * @author <a href="mailto:r979668507@gmail.com">renjp</a>
 * @date 2022/7/18 16:37
 */
public interface AuthorizationListener {
    boolean authorize(HandshakeData data);
}
