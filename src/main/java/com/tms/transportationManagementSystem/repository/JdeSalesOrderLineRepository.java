package com.tms.transportationManagementSystem.repository;

import com.tms.transportationManagementSystem.entity.JdeSalesOrder;
import com.tms.transportationManagementSystem.entity.JdeSalesOrderLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JdeSalesOrderLineRepository
        extends JpaRepository<JdeSalesOrderLine, Long> {
    List<JdeSalesOrderLine> findBySalesOrder(JdeSalesOrder salesOrder);

}