package com.tms.transportationManagementSystem.repository;

import com.tms.transportationManagementSystem.entity.TransporterShipmentBidTracker;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransporterShipmentBidTrackerRepository
        extends JpaRepository<TransporterShipmentBidTracker, Long> {

    boolean existsByShipmentIdAndTransporterId(Long shipmentId, Long transporterId);

}