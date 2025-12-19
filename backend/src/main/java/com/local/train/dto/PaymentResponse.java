// PaymentResponse.java
package com.local.train.dto;

import com.local.train.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private PaymentStatus status;
    private String transactionId;
    private String message;
    private Double amount;
    private Long bookingId;
}