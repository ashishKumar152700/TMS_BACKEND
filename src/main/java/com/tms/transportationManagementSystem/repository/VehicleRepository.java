package com.tms.transportationManagementSystem.repository;

import com.tms.transportationManagementSystem.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehicleRepository
        extends JpaRepository<Vehicle, Long> {

    boolean existsByVehicleNumber(
            String vehicleNumber
    );

    List<Vehicle> findByTransporterId(Long transporterId);
}