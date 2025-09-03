package com.globalbooks.payment.service;

import com.globalbooks.payment.dto.PaymentRequest;
import com.globalbooks.payment.dto.PaymentResponse;
import com.globalbooks.payment.entity.Payment;
import com.globalbooks.payment.entity.PaymentStatus;
import com.globalbooks.payment.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Service for payment processing.
 * Handles idempotency, external provider integration, and event publishing.
 */
@Service
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
    private static final String IDEMPOTENCY_LOCK_PREFIX = "payment:idempotency:";

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private PaymentProviderSimulator paymentProvider; // Simulated for dev

    /**
     * Create a payment with idempotency.
     * @param request Payment request
     * @param idempotencyKey Idempotency key from header
     * @return Payment response
     */
    @Transactional
    public PaymentResponse createPayment(PaymentRequest request, String idempotencyKey) {
        logger.info("Creating payment for order {} with idempotency key {}", request.getOrderId(), idempotencyKey);

        // Check idempotency
        Optional<Payment> existing = paymentRepository.findByIdempotencyKey(idempotencyKey);
        if (existing.isPresent()) {
            logger.info("Idempotent request detected, returning existing payment {}", existing.get().getId());
            return mapToResponse(existing.get());
        }

        // Acquire Redis lock for external call
        String lockKey = IDEMPOTENCY_LOCK_PREFIX + idempotencyKey;
        Boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, "locked", 30, TimeUnit.SECONDS);
        if (!Boolean.TRUE.equals(locked)) {
            throw new RuntimeException("Payment processing in progress for idempotency key: " + idempotencyKey);
        }

        try {
            // Create payment record
            Payment payment = new Payment(request.getOrderId(), request.getAmount(),
                                        request.getCurrency(), idempotencyKey);
            payment = paymentRepository.save(payment);

            // Call external provider (simulated)
            String providerRef = paymentProvider.processPayment(request.getPaymentMethodToken(),
                                                              request.getAmount(), request.getCurrency());

            if (providerRef != null) {
                payment.setStatus(PaymentStatus.COMPLETED);
                payment.setProvider("StripeSimulator"); // Example
                payment.setProviderRef(providerRef);
                payment = paymentRepository.save(payment);

                // Publish event
                publishPaymentEvent("payment.completed", payment);
                logger.info("Payment {} completed successfully", payment.getId());
            } else {
                payment.setStatus(PaymentStatus.FAILED);
                payment = paymentRepository.save(payment);
                publishPaymentEvent("payment.failed", payment);
                logger.warn("Payment {} failed", payment.getId());
            }

            return mapToResponse(payment);
        } finally {
            redisTemplate.delete(lockKey);
        }
    }

    /**
     * Get payment by ID.
     */
    public Optional<PaymentResponse> getPayment(UUID paymentId) {
        return paymentRepository.findById(paymentId).map(this::mapToResponse);
    }

    /**
     * Process refund.
     */
    @Transactional
    public PaymentResponse refundPayment(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentId));

        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new RuntimeException("Cannot refund non-completed payment");
        }

        // Simulate refund
        payment.setStatus(PaymentStatus.REFUNDED);
        payment = paymentRepository.save(payment);

        publishPaymentEvent("payment.refunded", payment);
        return mapToResponse(payment);
    }

    /**
     * Process webhook from external provider.
     */
    @Transactional
    public void processWebhook(String providerRef, String status, String signature) {
        // Validate HMAC signature (simplified)
        if (!validateSignature(signature)) {
            throw new RuntimeException("Invalid webhook signature");
        }

        Optional<Payment> payment = paymentRepository.findByProviderRef(providerRef);
        if (payment.isPresent()) {
            Payment p = payment.get();
            if ("completed".equals(status) && p.getStatus() == PaymentStatus.PENDING) {
                p.setStatus(PaymentStatus.COMPLETED);
                paymentRepository.save(p);
                publishPaymentEvent("payment.completed", p);
            }
        }
    }

    private void publishPaymentEvent(String routingKey, Payment payment) {
        // Publish with publisher confirms
        rabbitTemplate.convertAndSend("globalbooks.events", routingKey, payment);
        logger.info("Published event {} for payment {}", routingKey, payment.getId());
    }

    private PaymentResponse mapToResponse(Payment payment) {
        return new PaymentResponse(payment.getId(), payment.getOrderId(), payment.getAmount(),
                                 payment.getCurrency(), payment.getStatus(), payment.getProvider(),
                                 payment.getProviderRef(), payment.getCreatedAt(), payment.getUpdatedAt());
    }

    private boolean validateSignature(String signature) {
        // TODO: Implement HMAC SHA256 validation with secret
        return "valid".equals(signature); // Simplified
    }
}