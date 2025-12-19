// RevenueReport.java
package com.local.train.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevenueReport {
    private LocalDate startDate;
    private LocalDate endDate;
    private Double totalRevenue;
    private Double totalRefunds;
    private Double netRevenue;
    private Integer totalBookings;
    private Integer cancelledBookings;
    private Map<String, Double> revenueByClass;
    private Map<LocalDate, Double> dailyRevenue;
}