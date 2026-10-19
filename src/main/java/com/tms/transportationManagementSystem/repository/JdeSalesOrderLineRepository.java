package com.tms.transportationManagementSystem.repository;

import com.tms.transportationManagementSystem.entity.JdeSalesOrder;
import com.tms.transportationManagementSystem.entity.JdeSalesOrderLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JdeSalesOrderLineRepository
        extends JpaRepository<JdeSalesOrderLine, Long> {

    List<JdeSalesOrderLine> findBySalesOrderId(Long salesOrderId);

    List<JdeSalesOrderLine> findBySalesOrder(JdeSalesOrder salesOrder);

    List<JdeSalesOrderLine> findBySalesOrder_Id(Long salesOrderId);
}