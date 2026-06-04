package com.tms.transportationManagementSystem.repository;

import com.tms.transportationManagementSystem.entity.ShipmentOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShipmentOrderRepository extends JpaRepository<ShipmentOrder, Long> {
    List<ShipmentOrder> findByShipmentId(Long shipmentId);

    Optional<ShipmentOrder>
    findByShipmentIdAndOrderId(
            Long shipmentId,
            Long orderId
    );
}