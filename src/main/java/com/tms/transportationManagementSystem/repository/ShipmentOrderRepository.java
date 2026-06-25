package com.tms.transportationManagementSystem.repository;

import com.tms.transportationManagementSystem.entity.ShipmentOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShipmentOrderRepository
        extends JpaRepository<ShipmentOrder, Long> {

    boolean existsByOrderId(Long orderId);
}