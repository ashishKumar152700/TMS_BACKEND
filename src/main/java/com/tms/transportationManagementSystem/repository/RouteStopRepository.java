package com.tms.transportationManagementSystem.repository;

import com.tms.transportationManagementSystem.entity.RouteStop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RouteStopRepository
        extends JpaRepository<RouteStop, Long> {

    List<RouteStop> findByStopNameIgnoreCase(String stopName);

}