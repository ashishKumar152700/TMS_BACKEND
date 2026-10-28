package com.tms.transportationManagementSystem.repository;

import com.tms.transportationManagementSystem.entity.Transporter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TransporterRepository extends JpaRepository<Transporter, Long> {

    boolean existsByEmail(String email);

    Optional<Transporter> findByAddressNumber(Long addressNumber);

    List<Transporter> findByAddressNumberIn(Collection<Long> addressNumbers);

    boolean existsByMobile(String mobile);

    boolean existsByGstNumber(String gstNumber);

    boolean existsByPanNumber(String panNumber);

    @Query("""
           select distinct t
           from Transporter t
           left join fetch t.vehicles
           """)
    List<Transporter> findAllWithVehicles();

    @Query("""
           select t
           from Transporter t
           left join fetch t.vehicles
           where t.id = :id
           """)
    Optional<Transporter> findByIdWithVehicles(Long id);


}