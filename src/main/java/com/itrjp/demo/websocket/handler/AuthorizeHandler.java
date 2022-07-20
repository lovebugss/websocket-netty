package com.itrjp.demo.websocket.handler;

import com.itrjp.demo.websocket.listener.AuthorizationListener;
import com.itrjp.demo.websocket.Configuration;
import com.itrjp.demo.websocket.HandshakeData;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * TODO
 *
 * @author <a href="mailto:r979668507@gmail.com">renjp</a>
 * @date 2022/7/11 17:26
 */
@ChannelHandler.Sharable
public class AuthorizeHandler extends ChannelInboundHandlerAdapter {
    private final Logger logger = LoggerFactory.getLogger(AuthorizeHandler.class);
    private final Configuration configuration;
    private final AuthorizationListener authorization;

    public AuthorizeHandler(Configuration configuration, AuthorizationListener authorization) {
        this.configuration = configuration;
        this.authorization = authorization;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("AuthorizeHandler#channelActive");
        super.channelActive(ctx);
    }

    private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse res) {
        // Generate an error page if response getStatus code is not OK (200).
        HttpResponseStatus responseStatus = res.status();
        if (responseStatus.code() != 200) {
            ByteBufUtil.writeUtf8(res.content(), responseStatus.toString());
            HttpUtil.setContentLength(res, res.content().readableBytes());
        }
        // Send the response and close the connection if necessary.
        boolean keepAlive = HttpUtil.isKeepAlive(req) && responseStatus.code() == 200;
        HttpUtil.setKeepAlive(res, keepAlive);
        ChannelFuture future = ctx.write(res); // Flushed in channelReadComplete()
        if (!keepAlive) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("AuthorizeHandler#channelRead, channel: [{}], message class: [{}]", ctx.channel().id(), msg.getClass());
        if (msg instanceof FullHttpRequest request) {
            // 错误请求
            if (!request.decoderResult().isSuccess()) {
                sendHttpResponse(ctx, request, new DefaultFullHttpResponse(request.protocolVersion(), BAD_REQUEST, ctx.alloc().buffer(0)));
                return;
            }

            // 非Get 请求
            if (!GET.equals(request.method())) {
                sendHttpResponse(ctx, request, new DefaultFullHttpResponse(request.protocolVersion(), FORBIDDEN, ctx.alloc().buffer(0)));
                return;
            }
            Channel channel = ctx.channel();
            // 鉴权操作
            QueryStringDecoder queryString = new QueryStringDecoder(request.uri());
            Map<String, List<String>> parameters = queryString.parameters();
            HandshakeData handshakeData = new HandshakeData(parameters, request.uri());
            logger.info("AuthorizeHandler#channelRead: FullHttpRequest");
            boolean authorize = authorization.authorize(handshakeData);
            if (!authorize) {
                // 鉴权成功
                HttpResponse res = new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.UNAUTHORIZED);
                channel.writeAndFlush(res).addListener(ChannelFutureListener.CLOSE);
                logger.info("unauthorized, data: {}", handshakeData);
                request.release();
                return;
            }
        }
        ctx.fireChannelRead(msg);
    }
}
