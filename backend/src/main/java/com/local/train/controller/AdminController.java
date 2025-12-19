package com.local.train.controller;

import com.local.train.dto.ScheduleRequest;
import com.local.train.dto.ScheduleResponse;
import com.local.train.dto.StationRequest;
import com.local.train.entity.Station;
import com.local.train.entity.TrainSchedule;
import com.local.train.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/stations")
    public ResponseEntity<Station> createStation(@RequestBody StationRequest request) {
        return ResponseEntity.ok(adminService.createStation(request));
    }

    @GetMapping("/stations")
    public ResponseEntity<List<Station>> getAllStations() {
        return ResponseEntity.ok(adminService.getAllStations());
    }

    @PostMapping("/schedules")
    public ResponseEntity<ScheduleResponse> createSchedule(@RequestBody ScheduleRequest request) {
        TrainSchedule schedule = adminService.createSchedule(request);
        return ResponseEntity.ok(convertToResponse(schedule));
    }

    private ScheduleResponse convertToResponse(TrainSchedule schedule) {
        return ScheduleResponse.builder()
                .id(schedule.getId())
                .trainName(schedule.getTrain().getTrainName())
                .trainNumber(schedule.getTrain().getTrainNumber())
                .sourceStation(schedule.getSourceStation().getStationName())
                .destinationStation(schedule.getDestinationStation().getStationName())
                .departureTime(schedule.getDepartureTime())
                .arrivalTime(schedule.getArrivalTime())
                .availableSeats(schedule.getAvailableSeats())
                .operatingDays(schedule.getOperatingDays())
                // Fares and Duration to be populated if available in entity or calculated
                .build();
    }
    
    // Additional admin endpoints can be added here
}
