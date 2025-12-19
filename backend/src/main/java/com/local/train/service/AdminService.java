// AdminService.java
package com.local.train.service;

import com.local.train.dto.*;
import com.local.train.entity.*;
import com.local.train.repository.*;
import com.local.train.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {
    
    private final TrainScheduleRepository scheduleRepository;
    private final StationRepository stationRepository;
    private final TrainRepository trainRepository;
    private final BookingRepository bookingRepository;
    private final TransactionRepository transactionRepository;
    
    @Transactional
    public TrainSchedule createSchedule(ScheduleRequest request) {
        Train train = trainRepository.findById(request.getTrainId())
                .orElseThrow(() -> new ResourceNotFoundException("Train not found"));
        
        Station source = stationRepository.findById(request.getSourceStationId())
                .orElseThrow(() -> new ResourceNotFoundException("Source station not found"));
        
        Station destination = stationRepository.findById(request.getDestinationStationId())
                .orElseThrow(() -> new ResourceNotFoundException("Destination station not found"));
        
        TrainSchedule schedule = new TrainSchedule();
        schedule.setTrain(train);
        schedule.setSourceStation(source);
        schedule.setDestinationStation(destination);
        schedule.setDepartureTime(request.getDepartureTime());
        schedule.setArrivalTime(request.getArrivalTime());
        schedule.setTotalSeats(request.getTotalSeats());
        schedule.setAvailableSeats(request.getTotalSeats());
        schedule.setOperatingDays(request.getOperatingDays());
        
        return scheduleRepository.save(schedule);
    }
    
    @Transactional
    public TrainSchedule updateSchedule(Long scheduleId, ScheduleRequest request) {
        TrainSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found"));
        
        final Long trainId = request.getTrainId();
        if (trainId != null) {
            Train train = trainRepository.findById(trainId)
                    .orElseThrow(() -> new ResourceNotFoundException("Train not found"));
            schedule.setTrain(train);
        }
        
        final Long sourceStationId = request.getSourceStationId();
        if (sourceStationId != null) {
            Station source = stationRepository.findById(sourceStationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Source station not found"));
            schedule.setSourceStation(source);
        }
        
        final Long destinationStationId = request.getDestinationStationId();
        if (destinationStationId != null) {
            Station destination = stationRepository.findById(destinationStationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Destination station not found"));
            schedule.setDestinationStation(destination);
        }
        
        if (request.getDepartureTime() != null) schedule.setDepartureTime(request.getDepartureTime());
        if (request.getArrivalTime() != null) schedule.setArrivalTime(request.getArrivalTime());
        if (request.getTotalSeats() != null) {
            int diff = request.getTotalSeats() - schedule.getTotalSeats();
            schedule.setTotalSeats(request.getTotalSeats());
            schedule.setAvailableSeats(schedule.getAvailableSeats() + diff);
        }
        if (request.getOperatingDays() != null) schedule.setOperatingDays(request.getOperatingDays());
        
        return scheduleRepository.save(schedule);
    }
    
    @Transactional
    public void deleteSchedule(Long scheduleId) {
        if (!scheduleRepository.existsById(scheduleId)) {
            throw new ResourceNotFoundException("Schedule not found");
        }
        scheduleRepository.deleteById(scheduleId);
    }
    
    @Transactional
    public Station createStation(StationRequest request) {
        Station station = new Station();
        station.setStationCode(request.getStationCode());
        station.setStationName(request.getStationName());
        station.setCity(request.getCity());
        station.setState(request.getState());
        
        return stationRepository.save(station);
    }

    public List<Station> getAllStations() {
        return stationRepository.findAll();
    }
    
    public List<BookingResponse> getBookingReport(LocalDate startDate, LocalDate endDate) {
        List<Booking> bookings;
        if (startDate != null && endDate != null) {
            bookings = bookingRepository.findByBookingDateBetween(
                    startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
        } else {
            bookings = bookingRepository.findAll();
        }
        
        return bookings.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public RevenueReport getRevenueReport(LocalDate startDate, LocalDate endDate) {
        List<Booking> bookings;
        if (startDate != null && endDate != null) {
            bookings = bookingRepository.findByBookingDateBetween(
                    startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
        } else {
            bookings = bookingRepository.findAll();
        }
        
        double totalRevenue = bookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED)
                .mapToDouble(Booking::getTotalFare)
                .sum();
        
        long totalBookings = bookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED)
                .count();
        
        long cancelledBookings = bookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.CANCELLED)
                .count();
        
        return RevenueReport.builder()
                .totalRevenue(totalRevenue)
                .totalBookings((int) totalBookings)
                .cancelledBookings((int) cancelledBookings)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }
    
    public List<TransactionResponse> getTransactionReport(LocalDate startDate, LocalDate endDate) {
        List<Transaction> transactions;
        if (startDate != null && endDate != null) {
            transactions = transactionRepository.findByCreatedAtBetween(
                    startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
        } else {
            transactions = transactionRepository.findAll();
        }

        return transactions.stream()
                .map(this::convertTransactionToResponse)
                .collect(Collectors.toList());
    }

    private BookingResponse convertToResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .pnrNumber(booking.getPnrNumber())
                .journeyDate(booking.getJourneyDate().toLocalDate())
                .sourceStation(booking.getSchedule().getSourceStation().getStationName())
                .destinationStation(booking.getSchedule().getDestinationStation().getStationName())
                .trainName(booking.getSchedule().getTrain().getTrainName())
                .travelClass(booking.getTravelClass().name())
                .bookingDate(booking.getBookingDate().toLocalDate())
                .totalFare(booking.getTotalFare())
                .status(booking.getStatus())
                .passengerCount(booking.getPassengers().size())
                .ticketPath(booking.getTicketPath())
                .build();
    }

    private TransactionResponse convertTransactionToResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .transactionId(transaction.getTransactionId())
                .amount(transaction.getAmount())
                .type(transaction.getType())
                .method(transaction.getMethod())
                .status(transaction.getStatus())
                .createdAt(transaction.getCreatedAt())
                .description(transaction.getDescription())
                .build();
    }
}
