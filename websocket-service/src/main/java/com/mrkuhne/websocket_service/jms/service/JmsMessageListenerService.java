package com.mrkuhne.websocket_service.jms.service;

import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.mrkuhne.websocket_service.model.SimpleMessage;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JmsMessageListenerService {

    public void priceInfoListenerFree(@Payload SimpleMessage message) {
        log.info("Price info received on free listener: {}", message);
    }

    public void priceInfoListenerPremium(@Payload SimpleMessage message) {
        log.info("Price info received on premium listener: {}", message);
    }
    
}
