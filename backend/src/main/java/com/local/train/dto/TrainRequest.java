// TrainRequest.java
package com.local.train.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainRequest {
    @NotBlank(message = "Train number is required")
    @Pattern(regexp = "^[A-Z0-9]{5,10}$", message = "Invalid train number format")
    private String trainNumber;
    
    @NotBlank(message = "Train name is required")
    private String trainName;
    
    private Integer totalCoaches;
    private Integer seatsPerCoach;
}