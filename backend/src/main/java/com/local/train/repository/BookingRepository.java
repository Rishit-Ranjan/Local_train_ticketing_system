// BookingRepository.java
package com.local.train.repository;

import com.local.train.entity.Booking;
import com.local.train.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByPnrNumber(String pnrNumber);
    List<Booking> findByUserId(Long userId);
    List<Booking> findByScheduleId(Long scheduleId);
    List<Booking> findByStatus(BookingStatus status);
    
    Optional<Booking> findByIdAndUserId(Long id, Long userId);
    
    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId AND b.status = :status")
    List<Booking> findByUserIdAndStatus(@Param("userId") Long userId, 
                                        @Param("status") BookingStatus status);
    
    @Query("SELECT b FROM Booking b WHERE b.journeyDate BETWEEN :startDate AND :endDate")
    List<Booking> findBookingsBetweenDates(@Param("startDate") LocalDateTime startDate, 
                                          @Param("endDate") LocalDateTime endDate);

    List<Booking> findByBookingDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT b FROM Booking b WHERE b.bookingDate BETWEEN :startDate AND :endDate")
    List<Booking> findBookingsByBookingDate(@Param("startDate") LocalDateTime startDate, 
                                           @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.schedule.id = :scheduleId AND b.status = 'CONFIRMED'")
    Long countConfirmedBookingsBySchedule(@Param("scheduleId") Long scheduleId);
    
    @Query("SELECT SUM(b.totalFare) FROM Booking b " +
           "WHERE b.status = 'CONFIRMED' " +
           "AND b.bookingDate BETWEEN :startDate AND :endDate")
    Double sumRevenueBetweenDates(@Param("startDate") LocalDateTime startDate, 
                                 @Param("endDate") LocalDateTime endDate);
}