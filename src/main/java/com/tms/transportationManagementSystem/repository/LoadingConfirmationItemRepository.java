package com.tms.transportationManagementSystem.repository;

import com.tms.transportationManagementSystem.entity.LoadingConfirmationItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoadingConfirmationItemRepository
        extends JpaRepository<LoadingConfirmationItem, Long> {
}
