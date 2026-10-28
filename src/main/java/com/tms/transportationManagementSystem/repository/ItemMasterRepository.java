package com.tms.transportationManagementSystem.repository;

import com.tms.transportationManagementSystem.entity.ItemMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ItemMasterRepository extends JpaRepository<ItemMaster, Long> {

    Optional<ItemMaster> findByItemNumber(String itemNumber);

    @Query("select i.itemNumber from ItemMaster i")
    List<String> findAllItemNumbers();
}