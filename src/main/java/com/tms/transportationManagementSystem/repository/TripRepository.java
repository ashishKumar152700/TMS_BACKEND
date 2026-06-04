package com.tms.transportationManagementSystem.repository;

import com.tms.transportationManagementSystem.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripRepository
        extends JpaRepository<Trip, Long> {
}