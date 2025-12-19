// BookingRequest.java
package com.local.train.dto;

import com.local.train.entity.PaymentMethod;
import com.local.train.entity.TravelClass;
import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {
    @NotNull(message = "Schedule ID is required")
    private Long scheduleId;
    
    @NotNull(message = "Travel class is required")
    private TravelClass travelClass;
    
    @NotNull(message = "Journey date is required")
    @FutureOrPresent(message = "Journey date must be today or in the future")
    private LocalDate journeyDate;
    
    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;
    
    @Valid
    @NotNull(message = "At least one passenger is required")
    @Min(value = 1, message = "At least one passenger is required")
    private List<PassengerDto> passengers;
}
