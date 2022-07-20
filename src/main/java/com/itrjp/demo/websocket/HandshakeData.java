package com.itrjp.demo.websocket;

import java.util.List;
import java.util.Map;

/**
 * TODO
 *
 * @author <a href="mailto:r979668507@gmail.com">renjp</a>
 * @date 2022/7/18 16:44
 */

public record HandshakeData(Map<String, List<String>> parameters, String uri) {

}
