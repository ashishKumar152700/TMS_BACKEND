package com.tms.transportationManagementSystem.serviceImpl;

import com.tms.transportationManagementSystem.dto.RequestDTO.CustomerRequestDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.CustomerResponseDTO;
import com.tms.transportationManagementSystem.entity.Customer;
import com.tms.transportationManagementSystem.repository.CustomerRepository;
import com.tms.transportationManagementSystem.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public CustomerResponseDTO createCustomer(CustomerRequestDTO dto) {

        Customer customer = Customer.builder()
                .addressNumber(dto.getAddressNumber())
                .companyCode(dto.getCompanyCode())
                .alphaName(dto.getAlphaName())
                .cm(dto.getCm())
                .longAddress(dto.getLongAddress())
                .industryClass(dto.getIndustryClass())
                .scheduleType(dto.getScheduleType())
                .individualTaxId(dto.getIndividualTaxId())
                .build();

        return mapToResponse(customerRepository.save(customer));
    }

    @Override
    public List<CustomerResponseDTO> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public CustomerResponseDTO getCustomerById(Long id) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        return mapToResponse(customer);
    }

    @Override
    public CustomerResponseDTO updateCustomer(Long id,
                                              CustomerRequestDTO dto) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        customer.setAddressNumber(dto.getAddressNumber());
        customer.setCompanyCode(dto.getCompanyCode());
        customer.setAlphaName(dto.getAlphaName());
        customer.setCm(dto.getCm());
        customer.setLongAddress(dto.getLongAddress());
        customer.setIndustryClass(dto.getIndustryClass());
        customer.setScheduleType(dto.getScheduleType());
        customer.setIndividualTaxId(dto.getIndividualTaxId());

        return mapToResponse(customerRepository.save(customer));
    }

    @Override
    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }

    private CustomerResponseDTO mapToResponse(Customer customer) {

        return CustomerResponseDTO.builder()
                .id(customer.getId())
                .addressNumber(customer.getAddressNumber())
                .companyCode(customer.getCompanyCode())
                .alphaName(customer.getAlphaName())
                .cm(customer.getCm())
                .longAddress(customer.getLongAddress())
                .industryClass(customer.getIndustryClass())
                .scheduleType(customer.getScheduleType())
                .individualTaxId(customer.getIndividualTaxId())
                .build();
    }
}