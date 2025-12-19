// PaymentRequest.java
package com.local.train.dto;

import com.local.train.entity.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    private Double amount;
    private Long userId;
    private Long bookingId;
    private PaymentMethod method;
    private String transactionId;
}