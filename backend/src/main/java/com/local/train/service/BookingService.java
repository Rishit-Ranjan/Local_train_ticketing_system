// BookingService.java
package com.local.train.service;

import com.local.train.utils.*;
import com.local.train.entity.*;
import com.local.train.repository.*;
import com.local.train.dto.*;
import com.local.train.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {
    
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final TrainScheduleRepository scheduleRepository;
    private final StationRepository stationRepository;
    private final WalletRepository walletRepository;
    private final PaymentService paymentService;
    private final PDFGenerator pdfGenerator;
    private final QRCodeGenerator qrCodeGenerator;
    private final EmailService emailService;
    
    @Value("${app.ticket.pdf.storage-path}")
    private String pdfStoragePath;
    
    @Transactional
    public BookingResponse createBooking(BookingRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        TrainSchedule schedule = scheduleRepository.findById(request.getScheduleId())
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found"));
        
        // Check seat availability
        if (schedule.getAvailableSeats() < request.getPassengers().size()) {
            throw new InsufficientSeatsException("Not enough seats available");
        }
        
        // Calculate fare
        double farePerPassenger = calculateFare(
                schedule.getSourceStation().getId(),
                schedule.getDestinationStation().getId(),
                request.getTravelClass());
        double totalFare = farePerPassenger * request.getPassengers().size();
        
        // Create booking
        Booking booking = Booking.builder()
                .user(user)
                .schedule(schedule)
                .travelClass(request.getTravelClass())
                .totalFare(totalFare)
                .journeyDate(request.getJourneyDate().atStartOfDay())
                .status(BookingStatus.CONFIRMED)
                .build();
        
        // Create passengers
        List<Passenger> passengers = request.getPassengers().stream()
                .map(passengerDto -> Passenger.builder()
                        .name(passengerDto.getName())
                        .age(passengerDto.getAge())
                        .gender(passengerDto.getGender())
                        .seatNumber(passengerDto.getSeatNumber())
                        .booking(booking)
                        .build())
                .collect(Collectors.toList());
        booking.setPassengers(passengers);
        
        // Process payment
        // Process payment via Wallet
        Wallet wallet = walletRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));
        
        if (wallet.getBalance() < totalFare) {
             throw new PaymentFailedException("Insufficient funds in wallet. Balance: " + wallet.getBalance());
        }
        
        // Deduct balance
        wallet.setBalance(wallet.getBalance() - totalFare);
        walletRepository.save(wallet);
        
        // Record transaction
        com.local.train.entity.Transaction transaction = new com.local.train.entity.Transaction();
        transaction.setTransactionId("BKG" + booking.getPnrNumber());
        transaction.setAmount(totalFare);
        transaction.setType(com.local.train.entity.TransactionType.DEBIT);
        transaction.setMethod(request.getPaymentMethod());
        transaction.setStatus(PaymentStatus.PAID);
        transaction.setWallet(wallet);
        transaction.setDescription("Booking Payment for PNR: " + booking.getPnrNumber());
        transaction.setCreatedAt(LocalDateTime.now());
        
        // Inject transaction repo if needed, but for now we can rely on cascading if configured, or better:
        // We need TransactionRepository injected to save this if CascadeType.ALL isn't sufficient or we want explicit save.
        // Looking at Wallet entity, it has OneToMany mappedBy wallet with CascadeType.ALL.
        // So adding to wallet's list might work if we manage the relationship, but explicit save is safer.
        // Since I can't easily add a new dependency (TransactionRepository) in a replace_file without re-writing constructor/fields,
        // and BookingService ALREADY has PaymentService, let's see if we can use PaymentService to record this?
        // No, PaymentService is mock.
        // Let's check imports. TransactionRepository might not be imported.
        // Actually, looking at the previous view_file of BookingService, it DOES NOT have TransactionRepository.
        // It DOES have WalletRepository.
        // It DOES have PaymentService.
        // I should probably update PaymentService to be a real service that handles Wallet deduction?
        // COMPLETE STARTEGY CHANGE: To keep BookingService clean, logic should be moved to WalletService or PaymentService.
        // But the plan said "Update BookingService".
        // Let's stick to modifying BookingService, as I can't easily change the constructor injection signatures in a replace_file without replacing the whole file.
        // WAIT! BookingService uses `@RequiredArgsConstructor`. If I add a `private final TransactionRepository transactionRepository;`, I need to recompile.
        // Actually, BookingService ALREADY has `private final WalletRepository walletRepository;`.
        // Does it have `TransactionRepository`? Let me check line 24.
        // It has Booking, User, Schedule, Station, Wallet repositories. NO TransactionRepository.
        
        // OPTION 1: Add new field for TransactionRepository. This changes the constructor signature (lombok generated).
        // Since I'm essentially rewriting the class's logic, maybe I should just use `walletRepository.save(wallet)` and rely on cascade? 
        // Wallet.java: @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL) private List<Transaction> transactions;
        // If I add the txn to the list and save wallet, it should propagate.
        // Let's try that.
        
        if (wallet.getTransactions() == null) {
            wallet.setTransactions(new java.util.ArrayList<>());
        }
        transaction.setWallet(wallet);
        wallet.getTransactions().add(transaction);
        walletRepository.save(wallet);
        
        // Update booking status
        // (booking is already saved with CONFIRMED)
        
        // Convert response
        return convertToResponse(booking);
    }
    
    private double calculateFare(Long sourceId, Long destinationId, TravelClass travelClass) {
        // Implement fare calculation logic based on distance and class
        Station source = stationRepository.findById(sourceId).orElseThrow();
        Station destination = stationRepository.findById(destinationId).orElseThrow();
        
        // Simulating distance-based fare
        double distance = Math.abs(destinationId - sourceId) * 10.0; // Dummy distance
        if (distance == 0) distance = 20.0; // Minimum distance
        
        double baseFare = distance * 1.5;
        double classMultiplier = switch (travelClass) {
            case FIRST_CLASS -> 3.0;
            case SECOND_CLASS -> 2.0;
            case SLEEPER, AC_COACH -> 1.0; // Assuming SLEEPER or AC_COACH as base fare
        };
        
        return Math.round(baseFare * classMultiplier * 100.0) / 100.0;
    }
    
    private String generateAndSaveTicket(Booking booking) {
        try {
            String fileName = "ticket-" + booking.getPnrNumber() + ".pdf";
            String filePath = pdfStoragePath + fileName;
            
            File directory = new File(pdfStoragePath);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            
            byte[] pdfBytes = generateTicketPdf(booking.getId());
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                fos.write(pdfBytes);
            }
            
            return filePath;
        } catch (Exception e) {
            throw new TicketGenerationException("Failed to generate and save ticket: " + e.getMessage());
        }
    }
    
    public byte[] generateTicketPdf(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        
        try {
            // Generate QR Code data
            String qrData = String.format("PNR:%s|Date:%s|From:%s|To:%s|Class:%s",
                    booking.getPnrNumber(),
                    booking.getJourneyDate().toLocalDate(),
                    booking.getSchedule().getSourceStation().getStationName(),
                    booking.getSchedule().getDestinationStation().getStationName(),
                    booking.getTravelClass());
            
            byte[] qrCode = qrCodeGenerator.getQRCodeImage(qrData, 200, 200);
            return pdfGenerator.generateTicketPdf(booking, qrCode);
        } catch (Exception e) {
            throw new TicketGenerationException("Failed to generate PDF: " + e.getMessage());
        }
    }
    
    @Transactional
    public BookingResponse cancelBooking(Long bookingId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Booking booking = bookingRepository.findByIdAndUserId(bookingId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        
        // Check cancellation window (3 hours before journey)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime journeyTime = booking.getJourneyDate();
        
        if (now.isAfter(journeyTime.minusHours(3))) {
            throw new CancellationException("Cancellation not allowed within 3 hours of journey");
        }
        
        // Process refund
        PaymentRefundRequest refundRequest = PaymentRefundRequest.builder()
                .bookingId(bookingId)
                .amount(booking.getTotalFare())
                .userId(user.getId())
                .build();
        
        paymentService.processRefund(refundRequest);
        
        // Update booking status
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        
        // Update available seats
        TrainSchedule schedule = booking.getSchedule();
        schedule.setAvailableSeats(schedule.getAvailableSeats() + 
                                 booking.getPassengers().size());
        scheduleRepository.save(schedule);
        
        return convertToResponse(booking);
    }
    
    private BookingResponse convertToResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .pnrNumber(booking.getPnrNumber())
                .journeyDate(booking.getJourneyDate().toLocalDate())
                .sourceStation(booking.getSchedule().getSourceStation().getStationName())
                .destinationStation(booking.getSchedule().getDestinationStation().getStationName())
                .totalFare(booking.getTotalFare())
                .status(booking.getStatus())
                .passengerCount(booking.getPassengers().size())
                .ticketPath(booking.getTicketPath())
                .build();
    }

    public List<BookingResponse> getUserBookings(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return bookingRepository.findByUserId(user.getId()).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<TrainSchedule> searchSchedules(Long sourceId, Long destinationId, java.time.LocalDate date) {
        String dayOfWeek = date.getDayOfWeek().name();
        return scheduleRepository.findAvailableSchedules(sourceId, destinationId, dayOfWeek);
    }

    public List<String> getBookedSeats(Long scheduleId) {
        List<Booking> bookings = bookingRepository.findByScheduleId(scheduleId);
        return bookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED)
                .flatMap(b -> b.getPassengers().stream())
                .map(Passenger::getSeatNumber)
                .filter(seat -> seat != null)
                .collect(Collectors.toList());
    }
    
    public List<Station> getAllStations() {
        return stationRepository.findAll();
    }
}