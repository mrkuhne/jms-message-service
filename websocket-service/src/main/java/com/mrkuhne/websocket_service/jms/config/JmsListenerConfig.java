package com.mrkuhne.websocket_service.jms.config;

import java.lang.reflect.Method;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.JmsListenerConfigurer;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerEndpointRegistrar;
import org.springframework.jms.config.MethodJmsListenerEndpoint;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;

import com.mrkuhne.websocket_service.jms.Constant;
import com.mrkuhne.websocket_service.jms.JmsMessageListener;
import com.mrkuhne.websocket_service.model.SimpleMessage;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class JmsListenerConfig implements JmsListenerConfigurer{

    @Qualifier("freeListenerFactory")
    private final JmsListenerContainerFactory<?> freeListenerFactory;

    @Qualifier("premiumListenerFactory")
    private final JmsListenerContainerFactory<?> premiumListenerFactory;   
     
    private final DefaultMessageHandlerMethodFactory jmsMessageHandlerConfig;

    private final JmsTopicConfig jmsTopicConfig;
    private final JmsMessageListener jmsMessageListener;

    @Override
    public void configureJmsListeners(JmsListenerEndpointRegistrar registrar) {
        try {
            Method freeUserMethod = JmsMessageListener.class.getMethod("freeMessageListener", SimpleMessage.class);
            Method premiumUserMethod = JmsMessageListener.class.getMethod("premiumMessageListener", SimpleMessage.class);

            MethodJmsListenerEndpoint freeListenerEndpoint = getMethodJmsListenerEndpoint(
                Constant.FREE_TOPIC_LISTENER_ID, jmsTopicConfig.getFREE_USER_TOPIC_DESTINATION(), freeUserMethod, Constant.FREE_TOPIC_SUB_ID);
            MethodJmsListenerEndpoint premiumListenerEndpoint = getMethodJmsListenerEndpoint(
                Constant.PREMIUM_TOPIC_LISTENER_ID, jmsTopicConfig.getPREMIUM_USER_TOPIC_DESTINATION(), premiumUserMethod, Constant.PREMIUM_TOPIC_SUB_ID);
            
            registrar.registerEndpoint(freeListenerEndpoint, freeListenerFactory);
            registrar.registerEndpoint(premiumListenerEndpoint, premiumListenerFactory);
        } catch (NoSuchMethodException e) {
            throw new UnsupportedOperationException("Unimplemented method 'configureJmsListeners'");
        }
    }

    @NotNull
    private MethodJmsListenerEndpoint getMethodJmsListenerEndpoint(
        String listenerId,
        String topicDestination,
        Method listenerMethod,
        String subscriptionId
        ) {
        MethodJmsListenerEndpoint jmsListenerUserEndpoint = new MethodJmsListenerEndpoint();
        jmsListenerUserEndpoint.setId(listenerId);
        jmsListenerUserEndpoint.setDestination(topicDestination);
        jmsListenerUserEndpoint.setMethod(listenerMethod);
        jmsListenerUserEndpoint.setBean(jmsMessageListener);
        jmsListenerUserEndpoint.setSubscription(subscriptionId);
        jmsListenerUserEndpoint.setMessageHandlerMethodFactory(jmsMessageHandlerConfig);
        return jmsListenerUserEndpoint;
    }
    
}
