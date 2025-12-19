// BookingResponse.java
package com.local.train.dto;

import com.local.train.entity.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private Long id;
    private String pnrNumber;
    private LocalDate journeyDate;
    private String sourceStation;
    private String destinationStation;
    private String trainName;
    private String travelClass;
    private Double totalFare;
    private BookingStatus status;
    private Integer passengerCount;
    private String ticketPath;
    private LocalDate bookingDate;
}