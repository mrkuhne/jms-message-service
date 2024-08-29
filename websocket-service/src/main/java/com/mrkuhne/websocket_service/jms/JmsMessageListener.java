package com.mrkuhne.websocket_service.jms;

import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.mrkuhne.websocket_service.model.SimpleMessage;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JmsMessageListener {

    public void freeMessageListener(@Payload SimpleMessage message) {
        log.info("Message received on free listener: {}", message);
    }

    public void premiumMessageListener(@Payload SimpleMessage message) {
        log.info("Message received on premium listener: {}", message);
    }
    
}