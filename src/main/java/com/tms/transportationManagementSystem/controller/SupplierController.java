package com.tms.transportationManagementSystem.controller;

import com.tms.transportationManagementSystem.dto.RequestDTO.SupplierRequestDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.SupplierResponseDTO;
import com.tms.transportationManagementSystem.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @PostMapping
    public SupplierResponseDTO createSupplier(
            @RequestBody SupplierRequestDTO requestDTO) {

        return supplierService.createSupplier(requestDTO);
    }

    @GetMapping
    public List<SupplierResponseDTO> getAllSuppliers() {
        return supplierService.getAllSuppliers();
    }

    @GetMapping("/{id}")
    public SupplierResponseDTO getSupplierById(
            @PathVariable Long id) {

        return supplierService.getSupplierById(id);
    }

    @PutMapping("/{id}")
    public SupplierResponseDTO updateSupplier(
            @PathVariable Long id,
            @RequestBody SupplierRequestDTO requestDTO) {

        return supplierService.updateSupplier(id, requestDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
    }
}