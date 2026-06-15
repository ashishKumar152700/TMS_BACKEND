package com.tms.transportationManagementSystem.repository;

import com.tms.transportationManagementSystem.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface RouteRepository extends JpaRepository<Route, Long> {

    boolean existsByRouteCode(String routeCode);

    List<Route> findByDestinationLocationIgnoreCase(String destinationLocation);

    List<Route> findBySourceLocationAndDestinationLocation(
            String sourceLocation,
            String destinationLocation
    );
}