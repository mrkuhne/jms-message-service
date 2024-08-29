package com.mrkuhne.websocket_service.jms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Configuration
@Getter
public class JmsTopicConfig {

    @Value("${jms.free-topic-destination}")
    public String FREE_USER_TOPIC_DESTINATION;
    @Value("${jms.premium-topic-destination}")
    public String PREMIUM_USER_TOPIC_DESTINATION;
    
}
