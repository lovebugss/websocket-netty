package com.itrjp.demo.config;

import com.itrjp.demo.websocket.listener.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * TODO
 *
 * @author <a href="mailto:r979668507@gmail.com">renjp</a>
 * @date 2022/7/11 18:59
 */
@Configuration
public class WebsocketAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    AuthorizationListener authorization() {
        return new DefaultAuthorization();
    }

    @Bean
    @ConditionalOnMissingBean
    OpenListener openListener() {
        return new DefaultListener();
    }

    @Bean
    @ConditionalOnMissingBean
    CloseListener closeListener() {
        return new DefaultListener();
    }
}
