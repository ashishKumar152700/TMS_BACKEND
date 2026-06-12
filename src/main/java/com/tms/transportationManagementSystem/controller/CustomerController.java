package com.tms.transportationManagementSystem.controller;

import com.tms.transportationManagementSystem.dto.ResponseDTO.CustomerSalesOrderResponseDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.JdeCustomerResponseDTO;
import com.tms.transportationManagementSystem.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/jde/customers")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/getAll")
    public List<JdeCustomerResponseDTO> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("/{addressNumber}/sales-orders")
    public CustomerSalesOrderResponseDTO getCustomerSalesOrders(
            @PathVariable Long addressNumber) {

        return customerService.getCustomerSalesOrders(addressNumber);
    }


    @GetMapping("/getAllWithOrders")
    public List<JdeCustomerResponseDTO> getAllCustomersWithOrders() {
        return customerService.getAllCustomersWithOrders();
    }
}