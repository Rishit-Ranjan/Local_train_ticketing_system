// PassengerDto.java
package com.local.train.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PassengerDto {
    @NotBlank(message = "Passenger name is required")
    private String name;
    
    @NotNull(message = "Age is required")
    @Min(value = 1, message = "Age must be at least 1")
    private Integer age;
    
    @NotBlank(message = "Gender is required")
    @Pattern(regexp = "^(MALE|FEMALE|OTHER)$", message = "Gender must be MALE, FEMALE or OTHER")
    private String gender;
    
    private String seatNumber;
}