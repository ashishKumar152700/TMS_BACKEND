package com.tms.transportationManagementSystem.repository;

import com.tms.transportationManagementSystem.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByAddressNumber(Long addressNumber);
}