package com.tms.transportationManagementSystem.repository;

import com.tms.transportationManagementSystem.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    Optional<Vehicle> findByVehicleNumber(String vehicleNumber);

    @Modifying
    @Query("delete from Vehicle v where v.transporter.id = :transporterId")
    void deleteByTransporterId(Long transporterId);

}