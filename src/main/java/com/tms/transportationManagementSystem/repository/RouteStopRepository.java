package com.tms.transportationManagementSystem.repository;

import com.tms.transportationManagementSystem.entity.RouteStop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteStopRepository
        extends JpaRepository<RouteStop, Long> {
}