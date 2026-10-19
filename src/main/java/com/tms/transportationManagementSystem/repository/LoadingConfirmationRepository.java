package com.tms.transportationManagementSystem.repository;

import com.tms.transportationManagementSystem.entity.LoadingConfirmation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoadingConfirmationRepository
        extends JpaRepository<LoadingConfirmation, Long> {

    Optional<LoadingConfirmation> findByTripId(Long tripId);

    boolean existsByTripId(Long tripId);
}
