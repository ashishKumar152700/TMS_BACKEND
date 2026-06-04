package com.tms.transportationManagementSystem.repository;

import com.tms.transportationManagementSystem.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}