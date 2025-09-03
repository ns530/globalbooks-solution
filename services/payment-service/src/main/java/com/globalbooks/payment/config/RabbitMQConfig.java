package com.globalbooks.payment.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ configuration for PaymentService.
 * Declares exchanges, queues, bindings for event publishing.
 * Rationale: Topic exchange for flexible routing, durable queues for reliability.
 */
@Configuration
public class RabbitMQConfig {

    public static final String EVENTS_EXCHANGE = "globalbooks.events";
    public static final String PAYMENT_COMPLETED_ROUTING_KEY = "globalbooks.payment.completed";
    public static final String PAYMENT_FAILED_ROUTING_KEY = "globalbooks.payment.failed";
    public static final String PAYMENT_REFUNDED_ROUTING_KEY = "globalbooks.payment.refunded";

    public static final String PAYMENT_EVENTS_QUEUE = "payments.events.processor";
    public static final String PAYMENT_DLQ = "payments.dlq";

    // Retry queues with TTL for exponential backoff
    public static final String PAYMENT_RETRY_QUEUE_1 = "payments.retry.1";
    public static final String PAYMENT_RETRY_QUEUE_2 = "payments.retry.2";
    public static final String PAYMENT_RETRY_QUEUE_3 = "payments.retry.3";

    @Bean
    public TopicExchange eventsExchange() {
        return new TopicExchange(EVENTS_EXCHANGE, true, false);
    }

    @Bean
    public Queue paymentEventsQueue() {
        return QueueBuilder.durable(PAYMENT_EVENTS_QUEUE)
                .withArgument("x-dead-letter-exchange", "globalbooks.dlx")
                .withArgument("x-dead-letter-routing-key", "payments.failed")
                .build();
    }

    @Bean
    public Queue paymentDlq() {
        return QueueBuilder.durable(PAYMENT_DLQ).build();
    }

    @Bean
    public Binding paymentCompletedBinding(Queue paymentEventsQueue, TopicExchange eventsExchange) {
        return BindingBuilder.bind(paymentEventsQueue).to(eventsExchange).with(PAYMENT_COMPLETED_ROUTING_KEY);
    }

    @Bean
    public Binding paymentFailedBinding(Queue paymentEventsQueue, TopicExchange eventsExchange) {
        return BindingBuilder.bind(paymentEventsQueue).to(eventsExchange).with(PAYMENT_FAILED_ROUTING_KEY);
    }

    @Bean
    public Binding paymentRefundedBinding(Queue paymentEventsQueue, TopicExchange eventsExchange) {
        return BindingBuilder.bind(paymentEventsQueue).to(eventsExchange).with(PAYMENT_REFUNDED_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        rabbitTemplate.setMandatory(true); // Publisher confirms
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                // Handle nack (message not routed)
                System.err.println("Message not acknowledged: " + cause);
            }
        });
        return rabbitTemplate;
    }
}