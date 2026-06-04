package com.tms.transportationManagementSystem.serviceImpl;

import com.tms.transportationManagementSystem.dto.RequestDTO.SupplierRequestDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.SupplierResponseDTO;
import com.tms.transportationManagementSystem.entity.Supplier;
import com.tms.transportationManagementSystem.repository.SupplierRepository;
import com.tms.transportationManagementSystem.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;

    @Override
    public SupplierResponseDTO createSupplier(SupplierRequestDTO dto) {

        Supplier supplier = Supplier.builder()
                .addressNumber(dto.getAddressNumber())
                .alphaName(dto.getAlphaName())
                .individualTaxId(dto.getIndividualTaxId())
                .longAddress(dto.getLongAddress())
                .industryClass(dto.getIndustryClass())
                .scheduleType(dto.getScheduleType())
                .taxId(dto.getTaxId())
                .build();

        return mapToResponse(supplierRepository.save(supplier));
    }

    @Override
    public List<SupplierResponseDTO> getAllSuppliers() {

        return supplierRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public SupplierResponseDTO getSupplierById(Long id) {

        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        return mapToResponse(supplier);
    }

    @Override
    public SupplierResponseDTO updateSupplier(Long id,
                                              SupplierRequestDTO dto) {

        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        supplier.setAddressNumber(dto.getAddressNumber());
        supplier.setAlphaName(dto.getAlphaName());
        supplier.setIndividualTaxId(dto.getIndividualTaxId());
        supplier.setLongAddress(dto.getLongAddress());
        supplier.setIndustryClass(dto.getIndustryClass());
        supplier.setScheduleType(dto.getScheduleType());
        supplier.setTaxId(dto.getTaxId());

        return mapToResponse(supplierRepository.save(supplier));
    }

    @Override
    public void deleteSupplier(Long id) {
        supplierRepository.deleteById(id);
    }

    private SupplierResponseDTO mapToResponse(Supplier supplier) {

        return SupplierResponseDTO.builder()
                .id(supplier.getId())
                .addressNumber(supplier.getAddressNumber())
                .alphaName(supplier.getAlphaName())
                .individualTaxId(supplier.getIndividualTaxId())
                .longAddress(supplier.getLongAddress())
                .industryClass(supplier.getIndustryClass())
                .scheduleType(supplier.getScheduleType())
                .taxId(supplier.getTaxId())
                .build();
    }
}