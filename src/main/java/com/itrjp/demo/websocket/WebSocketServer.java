package com.itrjp.demo.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * TODO
 *
 * @author <a href="mailto:r979668507@gmail.com">renjp</a>
 * @date 2022/7/11 15:55
 */
@Component
public class WebSocketServer {
    private final Logger logger = LoggerFactory.getLogger(WebSocketServer.class);

    private final Configuration configuration;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private final WebSocketServerInitializer serverInitializer;

    Class<? extends ServerChannel> channelClass;

    public WebSocketServer(Configuration configuration, WebSocketServerInitializer serverInitializer) {
        this.configuration = configuration;
        this.serverInitializer = serverInitializer;
    }


    public void start() {
        startAsync().awaitUninterruptibly();
    }

    private Future<Void> startAsync() {
        initialization();

        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup).channel(channelClass).handler(new LoggingHandler(LogLevel.INFO)).childHandler(serverInitializer);

        int port = configuration.getPort();
        return b.bind(port).addListener((future) -> {
            if (future.isSuccess()) {
                logger.info("Websocket server started at port: {}", port);
            } else {
                logger.info("Websocket server start failed at port: {}, error: {}", port, future.cause().getMessage());
            }
        });
    }

    private void initialization() {
        bossGroup = new NioEventLoopGroup(configuration.getBossNum());
        workerGroup = new NioEventLoopGroup(configuration.getWorkNum());
        channelClass = NioServerSocketChannel.class;
        serverInitializer.initialization(configuration);
    }

    public void stop() {
        logger.info("Websocket server stop...");
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}
