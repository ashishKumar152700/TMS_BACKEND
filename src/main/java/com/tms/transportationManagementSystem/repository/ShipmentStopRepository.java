package com.tms.transportationManagementSystem.repository;

import com.tms.transportationManagementSystem.entity.ShipmentStop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShipmentStopRepository extends JpaRepository<ShipmentStop, Long> {

    List<ShipmentStop> findByShipmentIdOrderBySequenceNo(Long shipmentId);
}