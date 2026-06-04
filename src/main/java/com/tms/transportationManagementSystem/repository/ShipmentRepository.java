package com.tms.transportationManagementSystem.repository;

import com.tms.transportationManagementSystem.entity.Shipment;
//import com.tms.transportationManagementSystem.entity.ShipmentOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShipmentRepository
        extends JpaRepository<Shipment, Long> {

//    List<ShipmentOrder> findByShipmentId(Long shipmentId);
}