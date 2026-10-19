package com.tms.transportationManagementSystem.repository;

import com.tms.transportationManagementSystem.entity.Transporter;
import com.tms.transportationManagementSystem.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    Optional<Vehicle> findByVehicleNumber(String vehicleNumber);

    List<Vehicle> findByStatus(String status);

    List<Vehicle> findByTransporterAndVehicleTypeAndStatus(
            Transporter transporter,
            String vehicleType,
            String status);
}