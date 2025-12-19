package com.local.train.controller;

import com.local.train.dto.BookingRequest;
import com.local.train.dto.BookingResponse;
import com.local.train.entity.Station;
import com.local.train.entity.TrainSchedule;
import com.local.train.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@RequestBody BookingRequest request, Authentication authentication) {
        return ResponseEntity.ok(bookingService.createBooking(request, authentication.getName()));
    }

    @GetMapping("/my-bookings")
    public ResponseEntity<List<BookingResponse>> getUserBookings(Authentication authentication) {
        return ResponseEntity.ok(bookingService.getUserBookings(authentication.getName()));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(bookingService.cancelBooking(id, authentication.getName()));
    }

    @GetMapping("/{id}/ticket")
    public ResponseEntity<byte[]> downloadTicket(@PathVariable Long id) {
        byte[] pdfContent = bookingService.generateTicketPdf(id);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "ticket.pdf");
        
        return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<TrainSchedule>> searchSchedules(
            @RequestParam Long sourceId,
            @RequestParam Long destinationId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(bookingService.searchSchedules(sourceId, destinationId, date));
    }
    
    @GetMapping("/schedules/{scheduleId}/seats")
    public ResponseEntity<List<String>> getBookedSeats(@PathVariable Long scheduleId) {
        return ResponseEntity.ok(bookingService.getBookedSeats(scheduleId));
    }

    @GetMapping("/stations")
    public ResponseEntity<List<Station>> getAllStations() {
        return ResponseEntity.ok(bookingService.getAllStations());
    }
}
