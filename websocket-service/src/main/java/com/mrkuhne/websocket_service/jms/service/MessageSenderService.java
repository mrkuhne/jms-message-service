package com.mrkuhne.websocket_service.jms.service;

import java.math.BigDecimal;
import java.util.Random;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.mrkuhne.websocket_service.jms.config.JmsTopicConfig;
import com.mrkuhne.websocket_service.model.SimpleMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class MessageSenderService {

    private final Random random = new Random();
    private final JmsTopicConfig jmsTopicConfig;
    private final JmsTemplate jmsTemplate;

    @Scheduled(fixedRate = 5000)
    private void messageEmitter() {
        jmsTemplate.convertAndSend(jmsTopicConfig.getPRICE_INFO_TOPIC_FREE(),
            messageCreator("Message", "Free", randomBigDecimal()));
        jmsTemplate.convertAndSend(jmsTopicConfig.getPRICE_INFO_TOPIC_PREMIUM(),
            messageCreator("Message", "Premium", randomBigDecimal()));
        log.info("Messages have been sent to free and premium topics");
    }

    private BigDecimal randomBigDecimal() {
        double randomDouble = random.nextDouble() * 100;
        return new BigDecimal(String.format("%.2f", randomDouble));
    }

    private SimpleMessage messageCreator(String body, String recipient, BigDecimal value) {
        return new SimpleMessage(body, recipient, value);
    }

}
