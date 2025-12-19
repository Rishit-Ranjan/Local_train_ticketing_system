// ScheduleRequest.java
package com.local.train.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleRequest {
    @NotNull(message = "Train ID is required")
    private Long trainId;
    
    @NotNull(message = "Source station ID is required")
    private Long sourceStationId;
    
    @NotNull(message = "Destination station ID is required")
    private Long destinationStationId;
    
    @NotNull(message = "Departure time is required")
    private LocalTime departureTime;
    
    @NotNull(message = "Arrival time is required")
    private LocalTime arrivalTime;
    
    @NotNull(message = "Total seats is required")
    @Min(value = 1, message = "Total seats must be at least 1")
    private Integer totalSeats;
    
    @NotEmpty(message = "Operating days are required")
    private List<String> operatingDays;
}