package com.globalbooks.payment.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO for payment creation request.
 * Validates input and ensures PCI-safe handling (no raw PAN).
 */
public class PaymentRequest {

    @NotNull(message = "Order ID is required")
    private UUID orderId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Amount must have at most 10 integer and 2 fractional digits")
    private BigDecimal amount;

    @NotBlank(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency must be 3 characters")
    private String currency;

    @NotBlank(message = "Payment method token is required")
    private String paymentMethodToken; // Tokenized card info, not raw PAN

    // Getters and setters
    public UUID getOrderId() { return orderId; }
    public void setOrderId(UUID orderId) { this.orderId = orderId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getPaymentMethodToken() { return paymentMethodToken; }
    public void setPaymentMethodToken(String paymentMethodToken) { this.paymentMethodToken = paymentMethodToken; }
}
