package com.tms.transportationManagementSystem.repository;

import com.tms.transportationManagementSystem.entity.Transporter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransporterRepository extends JpaRepository<Transporter, Long> {

    boolean existsByEmail(String email);
    Optional<Transporter> findByAddressNumber(
            Long addressNumber
    );

    boolean existsByMobile(String mobile);

    boolean existsByGstNumber(String gstNumber);

    boolean existsByPanNumber(String panNumber);
}