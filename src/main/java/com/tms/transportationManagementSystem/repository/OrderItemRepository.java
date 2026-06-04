package com.tms.transportationManagementSystem.repository;

import com.tms.transportationManagementSystem.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository
        extends JpaRepository<OrderItem, Long> {
}