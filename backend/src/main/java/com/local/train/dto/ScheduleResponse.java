// ScheduleResponse.java
package com.local.train.dto;

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
public class ScheduleResponse {
    private Long id;
    private String trainName;
    private String trainNumber;
    private String sourceStation;
    private String destinationStation;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private Integer availableSeats;
    private Double fareFirstClass;
    private Double fareSecondClass;
    private Double fareGeneral;
    private List<String> operatingDays;
    private Integer durationMinutes;
}