package com.mrkuhne.websocket_service.jms.config;

import com.mrkuhne.websocket_service.jms.service.JmsMessageListenerService;
import com.mrkuhne.websocket_service.model.SimpleMessage;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.JmsListenerConfigurer;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerEndpointRegistrar;
import org.springframework.jms.config.MethodJmsListenerEndpoint;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

import static com.mrkuhne.websocket_service.jms.Constant.*;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class JmsListenerConfig implements JmsListenerConfigurer {

    private final JmsListenerContainerFactory<?> freeListenerFactory;
    private final JmsListenerContainerFactory<?> premiumListenerFactory;
    private final DefaultMessageHandlerMethodFactory jmsMessageHandlerConfig;
    private final JmsTopicConfig jmsTopicConfig;
    private final JmsMessageListenerService jmsMessageListener;

    @Override
    public void configureJmsListeners(JmsListenerEndpointRegistrar registrar) {
        var priceInfoEndpointFree = getMethodJmsListenerEndpoint(
                PRICE_INFO_LISTENER_ID_FREE,
                jmsTopicConfig.getPriceInfoTopicDestinationFree(),
                PRICE_INFO_SUB_ID_FREE,
                ReflectionUtils.findMethod(JmsMessageListenerService.class, "priceInfoListenerFree", SimpleMessage.class)
        );
        var priceInfoEndpointPremium = getMethodJmsListenerEndpoint(
                PRICE_INFO_LISTENER_ID_PREMIUM,
                jmsTopicConfig.getPrinceInfoTopicDestinationPremium(),
                PRICE_INFO_SUB_ID_PREMIUM,
                ReflectionUtils.findMethod(JmsMessageListenerService.class, "priceInfoListenerPremium", SimpleMessage.class)
        );
        registrar.registerEndpoint(priceInfoEndpointFree, freeListenerFactory);
        registrar.registerEndpoint(priceInfoEndpointPremium, premiumListenerFactory);
    }

    @NotNull
    private MethodJmsListenerEndpoint getMethodJmsListenerEndpoint(
            String listenerId,
            String topicDestination,
            String subscriptionId,
            Method listenerMethod
    ) {
        var jmsListenerUserEndpoint = new MethodJmsListenerEndpoint();
        jmsListenerUserEndpoint.setId(listenerId);
        jmsListenerUserEndpoint.setDestination(topicDestination);
        jmsListenerUserEndpoint.setMethod(listenerMethod);
        jmsListenerUserEndpoint.setBean(jmsMessageListener);
        jmsListenerUserEndpoint.setSubscription(subscriptionId);
        jmsListenerUserEndpoint.setMessageHandlerMethodFactory(jmsMessageHandlerConfig);
        return jmsListenerUserEndpoint;
    }

}
