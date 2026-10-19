package com.tms.transportationManagementSystem.repository;

import com.tms.transportationManagementSystem.entity.VehicleInspection;
import com.tms.transportationManagementSystem.enums.InspectionReviewStatus;
import com.tms.transportationManagementSystem.enums.GuardVerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VehicleInspectionRepository
        extends JpaRepository<VehicleInspection, Long> {

    Optional<VehicleInspection> findByTripId(Long tripId);

    boolean existsByTripId(Long tripId);

    List<VehicleInspection> findByGuardVerificationStatus(GuardVerificationStatus status);

    List<VehicleInspection> findByGuardVerificationStatusIn(List<GuardVerificationStatus> statuses);

    List<VehicleInspection> findByReviewStatus(
            InspectionReviewStatus reviewStatus
    );
}