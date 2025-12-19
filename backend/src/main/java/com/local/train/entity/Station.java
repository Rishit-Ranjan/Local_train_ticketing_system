// Station.java
package com.local.train.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "stations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Station {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String stationCode;
    
    @Column(nullable = false)
    private String stationName;
    
    private String city;
    private String state;
    
    @OneToMany(mappedBy = "sourceStation")
    private List<TrainSchedule> departures;
    
    @OneToMany(mappedBy = "destinationStation")
    private List<TrainSchedule> arrivals;
}