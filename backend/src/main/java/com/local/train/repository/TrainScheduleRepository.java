// TrainScheduleRepository.java
package com.local.train.repository;

import com.local.train.entity.TrainSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface TrainScheduleRepository extends JpaRepository<TrainSchedule, Long> {
    
    @Query("SELECT ts FROM TrainSchedule ts " +
           "WHERE ts.sourceStation.id = :sourceId " +
           "AND ts.destinationStation.id = :destinationId " +
           "AND :dayOfWeek MEMBER OF ts.operatingDays " +
           "AND ts.availableSeats > 0 " +
           "ORDER BY ts.departureTime")
    List<TrainSchedule> findAvailableSchedules(
            @Param("sourceId") Long sourceId,
            @Param("destinationId") Long destinationId,
            @Param("dayOfWeek") String dayOfWeek);
    
    @Query("SELECT ts FROM TrainSchedule ts " +
           "WHERE ts.train.id = :trainId " +
           "AND ts.departureTime >= :startTime " +
           "AND ts.departureTime <= :endTime")
    List<TrainSchedule> findByTrainAndTimeRange(
            @Param("trainId") Long trainId,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime);
    
    List<TrainSchedule> findBySourceStationId(Long sourceStationId);
    List<TrainSchedule> findByDestinationStationId(Long destinationStationId);
    
    @Query("SELECT COUNT(ts) FROM TrainSchedule ts " +
           "WHERE ts.sourceStation.id = :stationId " +
           "OR ts.destinationStation.id = :stationId")
    Long countByStationInvolved(@Param("stationId") Long stationId);
}