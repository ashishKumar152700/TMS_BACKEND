package com.tms.transportationManagementSystem.repository;

import com.tms.transportationManagementSystem.entity.OrderRouteMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRouteMappingRepository
        extends JpaRepository<OrderRouteMapping, Long> {

    Optional<OrderRouteMapping> findByOrderId(Long orderId);
}