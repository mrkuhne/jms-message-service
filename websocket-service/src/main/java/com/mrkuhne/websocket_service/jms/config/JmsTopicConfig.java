package com.mrkuhne.websocket_service.jms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Configuration
@Getter
public class JmsTopicConfig {

    @Value("${jms.topic.price-info-free}")
    public String priceInfoTopicDestinationFree;
    @Value("${jms.topic.price-info-premium}")
    public String princeInfoTopicDestinationPremium;
    
}
