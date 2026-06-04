package com.tms.transportationManagementSystem.repository;

import com.tms.transportationManagementSystem.entity.VehicleAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleAssignmentRepository
        extends JpaRepository<VehicleAssignment, Long> {
}