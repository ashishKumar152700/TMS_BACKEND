package com.tms.transportationManagementSystem.repository;

import com.tms.transportationManagementSystem.entity.TransporterAppointmentLetter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransporterAppointmentLetterRepository
        extends JpaRepository<TransporterAppointmentLetter, Long> {

    Optional<TransporterAppointmentLetter> findByAppointmentNumber(
            String appointmentNumber
    );


    List<TransporterAppointmentLetter> findByStatusIgnoreCase(
            String status
    );

    List<TransporterAppointmentLetter> findByTransporter_IdOrderByCreatedAtDesc(
            Long transporterId
    );
    Optional<TransporterAppointmentLetter> findByTrip_Id(Long tripId);



    boolean existsByTrip_Id(Long tripId);

    List<TransporterAppointmentLetter> findByTrip_IdIn(Collection<Long> tripIds);
}