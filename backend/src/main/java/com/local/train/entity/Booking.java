// Booking.java
package com.local.train.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Entity
@Table(name = "bookings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String pnrNumber;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "schedule_id", nullable = false)
    private TrainSchedule schedule;
    
    @Enumerated(EnumType.STRING)
    private TravelClass travelClass;
    
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    private List<Passenger> passengers;
    
    private Double totalFare;
    
    @Enumerated(EnumType.STRING)
    private BookingStatus status = BookingStatus.CONFIRMED;
    
    private LocalDateTime bookingDate;
    private LocalDateTime journeyDate;
    
    private String qrCodePath;
    private String ticketPath;
    
    @PrePersist
    protected void onCreate() {
        bookingDate = LocalDateTime.now();
        pnrNumber = generatePNR();
    }
    
    private String generatePNR() {
        // A more random and less predictable PNR
        long number = ThreadLocalRandom.current().nextLong(100_000_000L, 1_000_000_000L);
        return "PNR" + number;
    }
}