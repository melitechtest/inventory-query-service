package com.meli.inventory.query.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Provides necessary bean definitions for the consumer side.
 * It only provides the message converter and relies on the Command Service
 * to declare the queue and exchange.
 */
@Configuration
public class RabbitMQConfig {
    
    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}