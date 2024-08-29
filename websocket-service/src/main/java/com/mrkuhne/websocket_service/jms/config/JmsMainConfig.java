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
        ActiveMQConnectionFactory connection = new ActiveMQConnectionFactory();
        connection.setBrokerURL(brokerUrl);
        connection.setUserName(username);
        connection.setPassword(password);
        return connection;
    }

    @Bean
    @Qualifier("freeListenerFactory")
    public DefaultJmsListenerContainerFactory freeListenerFactory(ConnectionFactory connectionFactory) {
        log.info("Listener containter factory for {} has been created", FREE_TOPIC_ID);
        return getJmsListenerContainerFactory(connectionFactory, FREE_TOPIC_ID);
    }

    @Bean
    @Qualifier("premiumListenerFactory")
    public DefaultJmsListenerContainerFactory premiumListenerFactory(ConnectionFactory connectionFactory) {
        log.info("Listener containter factory for {} has been created", PREMIUM_TOPIC_ID);
        return getJmsListenerContainerFactory(connectionFactory, PREMIUM_TOPIC_ID);
    }

    private DefaultJmsListenerContainerFactory getJmsListenerContainerFactory(ConnectionFactory connectionFactory, String topicId) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jacksonMessageConverter(objectMapper()));
        factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
        factory.setPubSubDomain(true);
        factory.setSubscriptionDurable(true);
        factory.setClientId(topicId);
        return factory;
    }

    @Bean
    public JmsTemplate jmsTemplate(ObjectMapper objectMapp) {
        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setConnectionFactory(connectionFactory());
        jmsTemplate.setMessageConverter(jacksonMessageConverter(objectMapper()));
        jmsTemplate.setPubSubDomain(true);
        return jmsTemplate;
    }

    @Bean
    public MessageConverter jacksonMessageConverter(ObjectMapper objectMapper) {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        converter.setObjectMapper(objectMapper);
        return converter;
    }

    private ObjectMapper objectMapper(){
        return new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true)
        .registerModule(new JavaTimeModule())
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }
    
}
