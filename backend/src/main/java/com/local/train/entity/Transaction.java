package com.local.train.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String transactionId;

    @Column(nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(length = 1000)
    private String description;

    private String referenceId; // For external payment gateway reference
    private String upiTransactionId; // For UPI transactions
    private String cardLastFourDigits; // For card transactions
    private String bankName; // For net banking

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    private LocalDateTime refundDate;

    @Column(length = 1000)
    private String failureReason;

    @Column(length = 2000)
    private String metadata; // JSON string for additional transaction data

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (transactionId == null) {
            transactionId = generateTransactionId();
        }
        if (status == null) {
            status = PaymentStatus.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    private String generateTransactionId() {
        // Using UUID is a more robust way to generate unique IDs.
        return type.name().substring(0, 2) + "-" + UUID.randomUUID().toString();
    }

    // Helper methods
    public boolean isSuccessful() {
        return status == PaymentStatus.PAID || status == PaymentStatus.REFUNDED;
    }

    public boolean isRefundable() {
        return status == PaymentStatus.PAID && 
               type == TransactionType.DEBIT &&
               createdAt.isAfter(LocalDateTime.now().minusDays(30)); // Refund within 30 days
    }

    public String getMaskedCardNumber() {
        if (cardLastFourDigits != null && cardLastFourDigits.length() == 4) {
            return "**** **** **** " + cardLastFourDigits;
        }
        return null;
    }
}