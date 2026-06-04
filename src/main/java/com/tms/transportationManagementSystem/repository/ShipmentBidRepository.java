package com.tms.transportationManagementSystem.repository;

import com.tms.transportationManagementSystem.entity.ShipmentBid;
import enums.ShipmentBidStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShipmentBidRepository
        extends JpaRepository<ShipmentBid, Long> {

    List<ShipmentBid> findByShipmentId(Long shipmentId);

    Optional<ShipmentBid> findByShipmentIdAndStatus(
            Long shipmentId,
            ShipmentBidStatus status
    );
}