package com.globalbooks.payment.dto;

import com.globalbooks.payment.entity.PaymentStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * DTO for payment response.
 * Excludes sensitive data and includes all necessary fields.
 */
public class PaymentResponse {

    private UUID id;
    private UUID orderId;
    private BigDecimal amount;
    private String currency;
    private PaymentStatus status;
    private String provider;
    private String providerRef;
    private Instant createdAt;
    private Instant updatedAt;

    // Constructor from entity
    public PaymentResponse(UUID id, UUID orderId, BigDecimal amount, String currency,
                          PaymentStatus status, String provider, String providerRef,
                          Instant createdAt, Instant updatedAt) {
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

    // Getters
    public UUID getId() { return id; }
    public UUID getOrderId() { return orderId; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public PaymentStatus getStatus() { return status; }
    public String getProvider() { return provider; }
    public String getProviderRef() { return providerRef; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}