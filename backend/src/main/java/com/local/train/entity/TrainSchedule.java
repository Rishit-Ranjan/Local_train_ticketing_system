// TrainSchedule.java
package com.local.train.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "train_schedules")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "train_id", nullable = false)
    private Train train;
    
    @ManyToOne
    @JoinColumn(name = "source_station_id", nullable = false)
    private Station sourceStation;
    
    @ManyToOne
    @JoinColumn(name = "destination_station_id", nullable = false)
    private Station destinationStation;
    
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    
    private Integer totalSeats;
    private Integer availableSeats;
    
    @OneToMany(mappedBy = "schedule")
    private List<Booking> bookings;
    
    @ElementCollection
    @CollectionTable(name = "schedule_days")
    @Column(name = "day_of_week")
    private List<String> operatingDays; // MONDAY, TUESDAY, etc.
}