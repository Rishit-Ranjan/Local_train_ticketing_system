// AddFundsRequest.java
package com.local.train.dto;

import com.local.train.entity.PaymentMethod;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddFundsRequest {
    @NotNull(message = "Amount is required")
    @Min(value = 10, message = "Minimum amount is 10")
    private Double amount;
    
    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;
}