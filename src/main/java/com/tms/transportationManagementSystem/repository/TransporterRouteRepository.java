package com.tms.transportationManagementSystem.repository;

import com.tms.transportationManagementSystem.entity.TransporterRoute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransporterRouteRepository
        extends JpaRepository<TransporterRoute, Long> {

    boolean existsByTransporterIdAndRouteId(
            Long transporterId,
            Long routeId);
    List<TransporterRoute> findByTransporterId(Long transporterId);
}