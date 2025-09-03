package com.globalbooks.payment.controller;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.globalbooks.payment.dto.PaymentRequest;
import com.globalbooks.payment.dto.PaymentResponse;
import com.globalbooks.payment.service.PaymentService;

import io.micrometer.core.annotation.Timed;
import jakarta.validation.Valid;

/**
 * REST controller for payment operations.
 * Includes idempotency, validation, and observability.
 */
@RestController
@RequestMapping("/payments")
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private PaymentService paymentService;

    /**
     * Create a payment.
     * Idempotent via Idempotency-Key header.
     */
    @PostMapping
    @Timed(value = "payment.create", description = "Time taken to create payment")
    public ResponseEntity<PaymentResponse> createPayment(
            @Valid @RequestBody PaymentRequest request,
            @RequestHeader(value = "Idempotency-Key", required = true) String idempotencyKey) {

        logger.info("Received payment creation request for order {}", request.getOrderId());
        PaymentResponse response = paymentService.createPayment(request, idempotencyKey);
        return ResponseEntity.ok(response);
    }

    /**
     * Get payment by ID.
     */
    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable UUID paymentId) {
        return paymentService.getPayment(paymentId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Refund a payment.
     */
    @PostMapping("/{paymentId}/refund")
    @Timed(value = "payment.refund", description = "Time taken to refund payment")
    public ResponseEntity<PaymentResponse> refundPayment(@PathVariable UUID paymentId) {
        logger.info("Received refund request for payment {}", paymentId);
        PaymentResponse response = paymentService.refundPayment(paymentId);
        return ResponseEntity.ok(response);
    }

    /**
     * Webhook endpoint for external provider.
     */
    @PostMapping("/webhooks/payments")
    public ResponseEntity<Void> processWebhook(
            @RequestParam String providerRef,
            @RequestParam String status,
            @RequestHeader("X-Signature") String signature) {

        logger.info("Received webhook for provider ref {}", providerRef);
        paymentService.processWebhook(providerRef, status, signature);
        return ResponseEntity.ok().build();
    }
}
