// PaymentRefundRequest.java
package com.local.train.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRefundRequest {
    private Long bookingId;
    private Double amount;
    private Long userId;
    private String reason;
}