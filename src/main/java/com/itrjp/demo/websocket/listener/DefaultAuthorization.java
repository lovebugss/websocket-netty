package com.itrjp.demo.websocket.listener;

import com.itrjp.demo.websocket.HandshakeData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * TODO
 *
 * @author <a href="mailto:r979668507@gmail.com">renjp</a>
 * @date 2022/7/18 16:38
 */

public class DefaultAuthorization implements AuthorizationListener {
    private Logger logger = LoggerFactory.getLogger(DefaultAuthorization.class);

    @Override
    public boolean authorize(HandshakeData handshakeData) {
        logger.info("DefaultAuthorization#authorize, handshakeData: {}", handshakeData);
        Map<String, List<String>> parameters = handshakeData.parameters();
        return parameters.containsKey("cid");
    }
}
