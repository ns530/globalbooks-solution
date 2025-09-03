package com.globalbooks.payment.service;

import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Simulated payment provider for development.
 * Rationale: Avoid real API calls in dev; simulate success/failure for testing.
 */
@Component
public class PaymentProviderSimulator {

    /**
     * Simulate payment processing.
     * @param token Payment method token
     * @param amount Amount
     * @param currency Currency
     * @return Provider reference or null on failure
     */
    public String processPayment(String token, BigDecimal amount, String currency) {
        // Simulate processing delay
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Simulate 90% success rate
        if (Math.random() > 0.1) {
            return "sim_ref_" + UUID.randomUUID().toString().substring(0, 8);
        } else {
            return null; // Failure
        }
    }
}