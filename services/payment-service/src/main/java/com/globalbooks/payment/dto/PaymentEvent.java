package com.globalbooks.payment.dto;

import com.globalbooks.payment.entity.Payment;
import com.globalbooks.payment.entity.PaymentStatus;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Event DTO used for publishing to RabbitMQ.
 * FIXED: Avoids JavaTime (Instant) serialization issues by using ISO-8601 strings.
 * This decouples on-wire schema from internal entity representation.
 */
public class PaymentEvent { // FIXED: New DTO for message publishing

    private UUID id;
    private UUID orderId;
    private BigDecimal amount;
    private String currency;
    private PaymentStatus status;
    private String provider;
    private String providerRef;
    private String createdAt; // ISO-8601 string
    private String updatedAt; // ISO-8601 string

    public PaymentEvent() {
    }

    public PaymentEvent(UUID id,
                        UUID orderId,
                        BigDecimal amount,
                        String currency,
                        PaymentStatus status,
                        String provider,
                        String providerRef,
                        String createdAt,
                        String updatedAt) {
        this.id = id;
        this.orderId = orderId;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.provider = provider;
        this.providerRef = providerRef;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Factory method to build event from entity
    public static PaymentEvent from(Payment p) { // FIXED: helper to map entity -> event
        return new PaymentEvent(
                p.getId(),
                p.getOrderId(),
                p.getAmount(),
                p.getCurrency(),
                p.getStatus(),
                p.getProvider(),
                p.getProviderRef(),
                p.getCreatedAt() != null ? p.getCreatedAt().toString() : null, // FIXED: use ISO-8601 string
                p.getUpdatedAt() != null ? p.getUpdatedAt().toString() : null  // FIXED: use ISO-8601 string
        );
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getOrderId() { return orderId; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public PaymentStatus getStatus() { return status; }
    public String getProvider() { return provider; }
    public String getProviderRef() { return providerRef; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }

    // Setters (for serializer)
    public void setId(UUID id) { this.id = id; }
    public void setOrderId(UUID orderId) { this.orderId = orderId; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setCurrency(String currency) { this.currency = currency; }
    public void setStatus(PaymentStatus status) { this.status = status; }
    public void setProvider(String provider) { this.provider = provider; }
    public void setProviderRef(String providerRef) { this.providerRef = providerRef; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}