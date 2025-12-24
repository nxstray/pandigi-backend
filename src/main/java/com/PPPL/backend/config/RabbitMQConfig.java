package com.PPPL.backend.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Queue Names
    public static final String QUEUE_WS_ADMIN = "queue.ws.admin";
    public static final String QUEUE_EMAIL = "queue.email";

    // Exchange
    public static final String EXCHANGE_NOTIFICATION = "exchange.notification";

    // Routing Keys
    public static final String ROUTING_KEY_ADMIN = "routing.admin";
    public static final String ROUTING_KEY_EMAIL = "routing.email";

    /**
     * Queue untuk broadcast notifikasi admin via WebSocket
     */
    @Bean
    public Queue queueWsAdmin() {
        return new Queue(QUEUE_WS_ADMIN, true);
    }

    /**
     * Queue untuk kirim email
     */
    @Bean
    public Queue queueEmail() {
        return new Queue(QUEUE_EMAIL, true);
    }

    /**
     * Topic Exchange untuk routing
     */
    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(EXCHANGE_NOTIFICATION);
    }

    /**
     * Binding: exchange -> queue admin
     */
    @Bean
    public Binding bindingWsAdmin(Queue queueWsAdmin, TopicExchange notificationExchange) {
        return BindingBuilder
                .bind(queueWsAdmin)
                .to(notificationExchange)
                .with(ROUTING_KEY_ADMIN);
    }

    /**
     * Binding: exchange -> queue email
     */
    @Bean
    public Binding bindingEmail(Queue queueEmail, TopicExchange notificationExchange) {
        return BindingBuilder
                .bind(queueEmail)
                .to(notificationExchange)
                .with(ROUTING_KEY_EMAIL);
    }

    /**
     * JSON Message Converter
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * RabbitTemplate with JSON converter
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}   