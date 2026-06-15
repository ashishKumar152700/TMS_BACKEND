package com.tms.transportationManagementSystem.integration.jde.serviceImpl;

import com.tms.transportationManagementSystem.entity.Customer;
import com.tms.transportationManagementSystem.integration.jde.client.JdeCustomerClient;
import com.tms.transportationManagementSystem.integration.jde.dto.response.JdeCustomerApiResponse;
import com.tms.transportationManagementSystem.integration.jde.dto.response.JdeCustomerResponseDTO;
import com.tms.transportationManagementSystem.integration.jde.service.JdeCustomerSyncService;
import com.tms.transportationManagementSystem.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class JdeCustomerSyncServiceImpl implements JdeCustomerSyncService {

    private final JdeCustomerClient jdeCustomerClient;
    private final CustomerRepository customerRepository;

    @Override
    public void syncCustomers() {

        JdeCustomerApiResponse response = jdeCustomerClient.getCustomers();

        if (response == null || response.getCustomerData() == null) {
            log.info("No customer data received from JDE");
            return;
        }

        for (JdeCustomerResponseDTO dto : response.getCustomerData()) {

            Customer customer = customerRepository
                    .findByAddressNumber(dto.getAddressNumber())
                    .orElse(new Customer());

            customer.setAddressNumber(dto.getAddressNumber());
            customer.setAlphaName(dto.getCustomerName());
            customer.setLongAddress(dto.getAddress());
            customer.setIndividualTaxId(dto.getPanNumber());

            customer.setGstNumber(dto.getGstNumber());
            customer.setContactPerson(dto.getContactPerson());
            customer.setMobile(dto.getMobile());
            customer.setEmail(dto.getEmail());
            customer.setCity(dto.getCity());
            customer.setState(dto.getState());
            customer.setPinCode(dto.getPinCode());
            customer.setCountry(dto.getCountry());
            customer.setActive(dto.getActive());
            customer.setRemarks(dto.getRemarks());

            customerRepository.save(customer);
        }

        log.info("Customer Sync Completed. Total Records : {}",
                response.getCustomerData().size());
    }
}