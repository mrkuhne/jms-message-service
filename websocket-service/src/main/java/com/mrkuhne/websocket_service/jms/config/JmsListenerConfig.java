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
import com.mrkuhne.websocket_service.jms.service.JmsMessageListenerService;
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
    private final JmsMessageListenerService jmsMessageListener;

    @Override
    public void configureJmsListeners(JmsListenerEndpointRegistrar registrar) {
        try {
            Method priceInfoListenerMethodFree = JmsMessageListenerService.class.getMethod("priceInfoListenerFree", SimpleMessage.class);
            Method priceInfoListenerMethodPremium = JmsMessageListenerService.class.getMethod("priceInfoListenerPremium", SimpleMessage.class);

            MethodJmsListenerEndpoint priceInfoEndpointFree = getMethodJmsListenerEndpoint(
                Constant.PRICE_INFO_LISTENER_ID_FREE, jmsTopicConfig.getPRICE_INFO_TOPIC_FREE(), priceInfoListenerMethodFree, Constant.PRICE_INFO_SUB_ID_FREE);
            MethodJmsListenerEndpoint priceInfoEndpointPremium = getMethodJmsListenerEndpoint(
                Constant.PRICE_INFO_LISTENER_ID_PREMIUM, jmsTopicConfig.getPRICE_INFO_TOPIC_PREMIUM(), priceInfoListenerMethodPremium, Constant.PRICE_INFO_SUB_ID_PREMIUM);
            
            registrar.registerEndpoint(priceInfoEndpointFree, freeListenerFactory);
            registrar.registerEndpoint(priceInfoEndpointPremium, premiumListenerFactory);
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
