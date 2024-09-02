package com.mrkuhne.websocket_service.jms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
public class JmsMessageHandlerConfig {

    @Bean
    public DefaultMessageHandlerMethodFactory messageHandlerMethodFactory() {
        var factory = new DefaultMessageHandlerMethodFactory();
        factory.setValidator(new LocalValidatorFactoryBean());
        return factory;
    }
    
}
