package com.tms.transportationManagementSystem.repository;

import com.tms.transportationManagementSystem.entity.GatePass;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GatePassRepository extends JpaRepository<GatePass, Long> {

    Optional<GatePass> findByGateId(String gateId);

    Optional<GatePass> findByVehicleInspectionId(Long vehicleInspectionId);

    boolean existsByVehicleInspectionId(Long vehicleInspectionId);
}