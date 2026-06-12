package com.tms.transportationManagementSystem.serviceImpl;

import com.tms.transportationManagementSystem.dto.ResponseDTO.CustomerSalesOrderResponseDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.JdeCustomerResponseDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.JdeSalesOrderResponseDTO;
import com.tms.transportationManagementSystem.entity.JdeSalesOrder;
import com.tms.transportationManagementSystem.repository.CustomerRepository;
import com.tms.transportationManagementSystem.repository.JdeSalesOrderRepository;
import com.tms.transportationManagementSystem.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final JdeSalesOrderRepository salesOrderRepository;

    @Override
    public List<JdeCustomerResponseDTO> getAllCustomersWithOrders() {

        return customerRepository.findAll()
                .stream()
                .map(customer -> {

                    JdeCustomerResponseDTO dto =
                            new JdeCustomerResponseDTO();

                    dto.setId(customer.getId());
                    dto.setAddressNumber(customer.getAddressNumber());
                    dto.setAlphaName(customer.getAlphaName());
                    dto.setCm(customer.getCm());
                    dto.setCompanyCode(customer.getCompanyCode());
                    dto.setIndividualTaxId(customer.getIndividualTaxId());
                    dto.setIndustryClass(customer.getIndustryClass());
                    dto.setLongAddress(customer.getLongAddress());
                    dto.setScheduleType(customer.getScheduleType());

                    List<JdeSalesOrderResponseDTO> salesOrders =
                            salesOrderRepository
                                    .findByCustomerNumber(
                                            String.valueOf(customer.getAddressNumber())
                                    )
                                    .stream()
                                    .map(order -> {

                                        JdeSalesOrderResponseDTO orderDto =
                                                new JdeSalesOrderResponseDTO();

                                        orderDto.setId(order.getId());
                                        orderDto.setOrderNumber(order.getOrderNumber());
                                        orderDto.setOrderType(order.getOrderType());
                                        orderDto.setCompany(order.getCompany());
                                        orderDto.setCustomerNumber(order.getCustomerNumber());

                                        orderDto.setOrderDate(
                                                order.getOrderDate() != null
                                                        ? order.getOrderDate().toString()
                                                        : null
                                        );

                                        orderDto.setRequestedDate(
                                                order.getRequestedDate() != null
                                                        ? order.getRequestedDate().toString()
                                                        : null
                                        );

                                        orderDto.setBranchPlant(
                                                order.getBranchPlant() != null
                                                        ? order.getBranchPlant().trim()
                                                        : null
                                        );

                                        orderDto.setStatus(order.getStatus());
                                        orderDto.setCurrencyCode(order.getCurrencyCode());

                                        return orderDto;
                                    })
                                    .toList();

                    dto.setSalesOrders(salesOrders);

                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public CustomerSalesOrderResponseDTO getCustomerSalesOrders(Long addressNumber) {

        var customer = customerRepository.findByAddressNumber(addressNumber)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        List<JdeSalesOrder> orders =
                salesOrderRepository.findByCustomerNumber(
                        String.valueOf(customer.getAddressNumber())
                );

        CustomerSalesOrderResponseDTO response =
                new CustomerSalesOrderResponseDTO();

        response.setAddressNumber(customer.getAddressNumber());
        response.setCustomerName(customer.getAlphaName());

        List<JdeSalesOrderResponseDTO> salesOrderDtos = orders.stream()
                .map(order -> {

                    JdeSalesOrderResponseDTO dto =
                            new JdeSalesOrderResponseDTO();

                    dto.setId(order.getId());
                    dto.setOrderNumber(order.getOrderNumber());
                    dto.setOrderType(order.getOrderType());
                    dto.setCompany(order.getCompany());
                    dto.setCustomerNumber(order.getCustomerNumber());
                    dto.setStatus(order.getStatus());
                    dto.setCurrencyCode(order.getCurrencyCode());

                    dto.setOrderDate(
                            order.getOrderDate() != null
                                    ? order.getOrderDate().toString()
                                    : null
                    );

                    dto.setRequestedDate(
                            order.getRequestedDate() != null
                                    ? order.getRequestedDate().toString()
                                    : null
                    );

                    dto.setBranchPlant(
                            order.getBranchPlant() != null
                                    ? order.getBranchPlant().trim()
                                    : null
                    );

                    return dto;
                })
                .toList();

        response.setSalesOrders(salesOrderDtos);

        return response;
    }

    @Override
    public List<JdeCustomerResponseDTO> getAllCustomers() {

        return customerRepository.findAll()
                .stream()
                .map(customer -> {

                    JdeCustomerResponseDTO dto =
                            new JdeCustomerResponseDTO();

                    dto.setId(customer.getId());
                    dto.setAddressNumber(customer.getAddressNumber());
                    dto.setAlphaName(customer.getAlphaName());
                    dto.setCm(customer.getCm());
                    dto.setCompanyCode(customer.getCompanyCode());
                    dto.setIndividualTaxId(customer.getIndividualTaxId());
                    dto.setIndustryClass(customer.getIndustryClass());
                    dto.setLongAddress(customer.getLongAddress());
                    dto.setScheduleType(customer.getScheduleType());

                    return dto;
                })
                .collect(Collectors.toList());
    }
}