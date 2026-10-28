package com.tms.transportationManagementSystem.integration.jde.serviceImpl;

import com.tms.transportationManagementSystem.entity.Customer;
import com.tms.transportationManagementSystem.integration.jde.client.JdeCustomerClient;
import com.tms.transportationManagementSystem.integration.jde.dto.response.JdeCustomerApiResponse;
import com.tms.transportationManagementSystem.integration.jde.dto.response.JdeCustomerResponseDTO;
import com.tms.transportationManagementSystem.integration.jde.service.JdeCustomerSyncService;
import com.tms.transportationManagementSystem.repository.CustomerRepository;
import com.tms.transportationManagementSystem.serviceImpl.SalesOrderLookup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

        // Existing address numbers ek hi query mein (pehle har customer ke liye findByAddressNumber)
        Set<Long> existingAddressNumbers =
                new HashSet<>(customerRepository.findAllAddressNumbers());

        List<Customer> newCustomers = new ArrayList<>();

        for (JdeCustomerResponseDTO dto : response.getCustomerData()) {

            // Sirf naya customer insert hoga. DB mein pehle se hai to skip -
            // TMS mein kiye gaye changes overwrite nahi honge.
            // add() false = DB mein hai ya isi response mein pehle aa chuka hai.
            if (!existingAddressNumbers.add(dto.getAddressNumber())) {
                continue;
            }

            Customer customer = new Customer();

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

            newCustomers.add(customer);
        }

        // Har chunk ek transaction mein save (pehle har row ka alag transaction)
        SalesOrderLookup.inChunks(newCustomers, customerRepository::saveAll);

        log.info("Customer Sync Completed. Total Records : {} | New Inserted : {}",
                response.getCustomerData().size(), newCustomers.size());
    }
}
