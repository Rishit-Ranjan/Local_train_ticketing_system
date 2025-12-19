// Passenger.java
package com.local.train.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "passengers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Passenger {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private Integer age;
    private String gender;
    
    private String seatNumber;
    
    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;
}
