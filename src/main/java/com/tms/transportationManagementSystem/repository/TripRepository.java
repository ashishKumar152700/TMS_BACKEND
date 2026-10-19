package com.tms.transportationManagementSystem.repository;

import com.tms.transportationManagementSystem.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TripRepository
        extends JpaRepository<Trip, Long> {

    Optional<Trip> findByShipmentId(Long shipmentId);
}