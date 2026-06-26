package com.tms.transportationManagementSystem.repository;

import com.tms.transportationManagementSystem.entity.ShipmentOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShipmentOrderRepository extends JpaRepository<ShipmentOrder, Long> {

    List<ShipmentOrder> findByShipmentId(Long shipmentId);

    boolean existsByOrderId(Long orderId);
}