// Train.java
package com.local.train.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "trains")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Train {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String trainNumber;
    
    @Column(nullable = false)
    private String trainName;
    
    private Integer totalCoaches;
    
    @OneToMany(mappedBy = "train")
    private List<TrainSchedule> schedules;
}
