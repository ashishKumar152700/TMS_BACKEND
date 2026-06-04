package com.tms.transportationManagementSystem.repository;

import com.tms.transportationManagementSystem.entity.VehicleRouteMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRouteMappingRepository
        extends JpaRepository<VehicleRouteMapping, Long> {

    boolean existsByVehicleIdAndRouteId(
            Long vehicleId,
            Long routeId);
}