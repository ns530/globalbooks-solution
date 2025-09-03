package com.globalbooks.payment.repository;

import com.globalbooks.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA repository for Payment entity.
 * Provides CRUD operations and custom queries for idempotency.
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    /**
     * Find payment by idempotency key.
     * Used for idempotency checks.
     */
    Optional<Payment> findByIdempotencyKey(String idempotencyKey);

    /**
     * Find payment by provider reference.
     * Used for webhook processing.
     */
    Optional<Payment> findByProviderRef(String providerRef);
}