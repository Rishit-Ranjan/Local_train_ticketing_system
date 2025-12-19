// StationRepository.java
package com.local.train.repository;

import com.local.train.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StationRepository extends JpaRepository<Station, Long> {
    Optional<Station> findByStationCode(String stationCode);
    List<Station> findByCity(String city);
    List<Station> findByState(String state);
    
    @Query("SELECT s FROM Station s WHERE LOWER(s.stationName) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "OR LOWER(s.city) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Station> searchByNameOrCity(@Param("name") String name);
}