package com.globalbooks.payment.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger; // FIXED: add logger for structured error reporting
import org.slf4j.LoggerFactory; // FIXED: add logger for structured error reporting
// FIXED: Configure Jackson for Java Time (Instant) serialization used in Payment entity
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * RabbitMQ configuration for PaymentService.
 * Declares exchanges, queues, bindings for event publishing.
 * Rationale: Topic exchange for flexible routing, durable queues for reliability.
 */
@Configuration
public class RabbitMQConfig {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConfig.class); // FIXED: add logger

    public static final String EVENTS_EXCHANGE = "globalbooks.events";
    public static final String PAYMENT_COMPLETED_ROUTING_KEY = "globalbooks.payment.completed";
    public static final String PAYMENT_FAILED_ROUTING_KEY = "globalbooks.payment.failed";
    public static final String PAYMENT_REFUNDED_ROUTING_KEY = "globalbooks.payment.refunded";

    public static final String PAYMENT_EVENTS_QUEUE = "payments.events.processor.v2"; // FIXED: rotate queue name to avoid pre-existing args conflict
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

    // FIXED: Declare DLX because queues reference it; prevents unroutable dead letters
    @Bean
    public TopicExchange dlxExchange() {
        return new TopicExchange("globalbooks.dlx", true, false); // durable DLX
    }

    // FIXED: Bind DLQ to DLX with routing key used by x-dead-letter-routing-key
    @Bean
    public Binding paymentDlqBinding(Queue paymentDlq, TopicExchange dlxExchange) {
        return BindingBuilder.bind(paymentDlq).to(dlxExchange).with("payments.failed");
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
        // FIXED: Register JavaTimeModule and disable timestamps so Instant serializes as ISO-8601
        ObjectMapper mapper = Jackson2ObjectMapperBuilder.json()
                .modules(new JavaTimeModule())
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
        return new Jackson2JsonMessageConverter(mapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        rabbitTemplate.setMandatory(true); // Publisher confirms
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                // FIXED: use structured logging, not stderr
                logger.error("RabbitMQ publish not acknowledged: correlationId={}, cause={}",
                        correlationData != null ? correlationData.getId() : null, cause);
            }
        });
        return rabbitTemplate;
    }
}