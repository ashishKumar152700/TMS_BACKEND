package com.tms.transportationManagementSystem.repository;

import com.tms.transportationManagementSystem.entity.SalesOrderLine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalesOrderLineRepository extends JpaRepository<SalesOrderLine, Long> {
}