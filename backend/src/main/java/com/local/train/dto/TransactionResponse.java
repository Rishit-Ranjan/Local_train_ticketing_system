package com.local.train.dto;

import com.local.train.entity.PaymentMethod;
import com.local.train.entity.PaymentStatus;
import com.local.train.entity.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private String transactionId;
    private Double amount;
    private TransactionType type;
    private PaymentMethod method;
    private PaymentStatus status;
    private LocalDateTime createdAt;
    private String description;
}