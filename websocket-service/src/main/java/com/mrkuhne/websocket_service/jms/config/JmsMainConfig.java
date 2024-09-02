package com.mrkuhne.websocket_service.jms.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.Session;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableJms
@RequiredArgsConstructor
@Slf4j
public class JmsMainConfig {

    private static final String FREE_TOPIC_ID = "free-topic";
    private static final String PREMIUM_TOPIC_ID = "premium-topic";

    @Value("${spring.activemq.broker-url}")
    private String brokerUrl;
    @Value("${spring.activemq.user}")
    private String username;
    @Value("${spring.activemq.password}")
    private String password;

    @Bean
    public ConnectionFactory connectionFactory() {
        return new ActiveMQConnectionFactory(username, password, brokerUrl);
    }

    @Bean
    public MessageConverter messageConverter() {
        var converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        converter.setObjectMapper(objectMapper());
        return converter;
    }

    @Bean
    public DefaultJmsListenerContainerFactory freeListenerFactory(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        log.info("Listener container factory for {} has been created", FREE_TOPIC_ID);
        return getJmsListenerContainerFactory(connectionFactory, messageConverter, FREE_TOPIC_ID);
    }

    @Bean
    public DefaultJmsListenerContainerFactory premiumListenerFactory(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        log.info("Listener container factory for {} has been created", PREMIUM_TOPIC_ID);
        return getJmsListenerContainerFactory(connectionFactory, messageConverter, PREMIUM_TOPIC_ID);
    }

    private DefaultJmsListenerContainerFactory getJmsListenerContainerFactory(ConnectionFactory connectionFactory, MessageConverter messageConverter, String topicId) {
        var factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
        factory.setPubSubDomain(true);
        factory.setSubscriptionDurable(true);
        factory.setClientId(topicId);
        return factory;
    }

    @Bean
    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        var jmsTemplate = new JmsTemplate();
        jmsTemplate.setConnectionFactory(connectionFactory);
        jmsTemplate.setMessageConverter(messageConverter);
        jmsTemplate.setPubSubDomain(true);
        return jmsTemplate;
    }

    private ObjectMapper objectMapper(){
        return new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true)
        .registerModule(new JavaTimeModule())
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }
    
}
