package com.tms.transportationManagementSystem.repository;

import com.tms.transportationManagementSystem.entity.JdeSalesOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JdeSalesOrderRepository
        extends JpaRepository<JdeSalesOrder, Long> {

    Optional<JdeSalesOrder> findByOrderNumber(String orderNumber);
}