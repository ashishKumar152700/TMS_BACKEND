package com.tms.transportationManagementSystem.service;

import com.tms.transportationManagementSystem.dto.RequestDTO.SupplierRequestDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.SupplierResponseDTO;

import java.util.List;

public interface SupplierService {

    SupplierResponseDTO createSupplier(SupplierRequestDTO requestDTO);

    List<SupplierResponseDTO> getAllSuppliers();

    SupplierResponseDTO getSupplierById(Long id);

    SupplierResponseDTO updateSupplier(Long id,
                                       SupplierRequestDTO requestDTO);

    void deleteSupplier(Long id);
}