package com.tms.transportationManagementSystem.repository;

import com.tms.transportationManagementSystem.entity.ShipmentBid;
import com.tms.transportationManagementSystem.enums.ShipmentBidStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShipmentBidRepository extends JpaRepository<ShipmentBid, Long> {

    List<ShipmentBid> findByShipmentId(Long shipmentId);

    List<ShipmentBid> findByTransporterId(Long transporterId);

    boolean existsByShipmentIdAndTransporterId(Long shipmentId,
                                               Long transporterId);

    List<ShipmentBid> findByTransporterIdOrderByBidDateDesc(Long transporterId);

    List<ShipmentBid> findByShipmentIdOrderByBidAmountAsc(Long shipmentId);

    List<ShipmentBid> findByShipmentIdAndBidStatusOrderByBidAmountAsc(
            Long shipmentId,
            ShipmentBidStatus bidStatus
    );
}