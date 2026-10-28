
package com.tms.transportationManagementSystem.repository;

import com.tms.transportationManagementSystem.entity.Customer;
import com.tms.transportationManagementSystem.entity.JdeSalesOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository
        extends JpaRepository<Customer, Long> {
    Optional<Customer> findByAddressNumber(Long addressNumber);

    @Query("select c.addressNumber from Customer c")
    List<Long> findAllAddressNumbers();

//    List<JdeSalesOrder> findByCustomerNumber(String customerNumber);
}