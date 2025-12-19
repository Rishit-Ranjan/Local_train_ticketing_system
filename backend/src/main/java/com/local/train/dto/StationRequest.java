// StationRequest.java
package com.local.train.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StationRequest {
    @NotBlank(message = "Station code is required")
    private String stationCode;
    
    @NotBlank(message = "Station name is required")
    private String stationName;
    
    private String city;
    private String state;
}