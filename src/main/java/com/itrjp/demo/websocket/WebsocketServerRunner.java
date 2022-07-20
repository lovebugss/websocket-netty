package com.itrjp.demo.websocket;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * TODO
 *
 * @author <a href="mailto:r979668507@gmail.com">renjp</a>
 * @date 2022/7/11 18:59
 */
@Component
public class WebsocketServerRunner implements CommandLineRunner, DisposableBean {
    private final WebSocketServer server;

    public WebsocketServerRunner(WebSocketServer server) {
        this.server = server;
    }

    @Override
    public void destroy() throws Exception {
        server.stop();
    }

    @Override
    public void run(String... args) throws Exception {
        server.start();
    }
}
