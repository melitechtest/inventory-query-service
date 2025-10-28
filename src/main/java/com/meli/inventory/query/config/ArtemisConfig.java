package com.meli.inventory.query.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import jakarta.jms.ConnectionFactory;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration class for ActiveMQ Artemis (JMS) listeners.
 * Configures the JMS listener to use Topics (Pub/Sub) and handles JSON message conversion.
 */
@Configuration
public class ArtemisConfig {

    @Bean
    public JmsListenerContainerFactory<?> jmsListenerContainerFactory(ConnectionFactory connectionFactory, DefaultJmsListenerContainerFactoryConfigurer configurer, MessageConverter messageConverter) {

        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        configurer.configure(factory, connectionFactory);

        factory.setPubSubDomain(true);
        factory.setConcurrency("3-10");
        factory.setMessageConverter(messageConverter);

        factory.setErrorHandler(t -> LoggerFactory.getLogger(ArtemisConfig.class).error("JMS listener error", t));

        return factory;
    }

    @Bean
    public MessageConverter jacksonJmsMessageConverter() {
        ObjectMapper mapper = new ObjectMapper();

        mapper.activateDefaultTyping(BasicPolymorphicTypeValidator.builder().allowIfBaseType("com.meli.inventory.query.dto").allowIfBaseType("com.meli.inventory.command.dto").allowIfBaseType("java.lang").allowIfBaseType("java.util").build(), ObjectMapper.DefaultTyping.NON_FINAL);

        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("__TypeId__");
        converter.setObjectMapper(mapper);

        Map<String, Class<?>> typeIdMappings = new HashMap<>();
        typeIdMappings.put("com.meli.inventory.command.dto.StockUpdateEvent", com.meli.inventory.query.dto.StockUpdateEvent.class);
        typeIdMappings.put("StockUpdateEvent", com.meli.inventory.query.dto.StockUpdateEvent.class);
        converter.setTypeIdMappings(typeIdMappings);

        return converter;
    }
}
