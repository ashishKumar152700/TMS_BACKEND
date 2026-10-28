package com.tms.transportationManagementSystem.integration.jde.serviceImpl;

import com.tms.transportationManagementSystem.dto.ResponseDTO.JdeSalesOrderLineDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.JdeSalesOrderResponseDTO;
import com.tms.transportationManagementSystem.entity.JdeSalesOrder;
import com.tms.transportationManagementSystem.entity.JdeSalesOrderLine;
import com.tms.transportationManagementSystem.integration.jde.client.JdeSalesOrderClient;
import com.tms.transportationManagementSystem.integration.jde.dto.response.JdeSalesOrderApiResponse;
import com.tms.transportationManagementSystem.integration.jde.service.JdeSalesOrderSyncService;
import com.tms.transportationManagementSystem.repository.JdeSalesOrderLineRepository;
import com.tms.transportationManagementSystem.repository.JdeSalesOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class JdeSalesOrderSyncServiceImpl implements JdeSalesOrderSyncService {

    private final JdeSalesOrderClient jdeSalesOrderClient;
    private final JdeSalesOrderRepository orderRepository;
    private final JdeSalesOrderLineRepository lineRepository;
    private final TransactionTemplate transactionTemplate;

    @Override
    public void syncSalesOrders() {

        // JDE call transaction ke bahar - slow API ke time DB connection hold nahi hota
        JdeSalesOrderApiResponse response =
                jdeSalesOrderClient.getSalesOrders();

        if (response == null || response.getOrders() == null) {
            return;
        }

        // Saara DB kaam pehle jaisa ek hi transaction mein
        transactionTemplate.executeWithoutResult(status -> saveNewOrders(response));

        log.info("Sales Orders Sync Completed");
    }

    private void saveNewOrders(JdeSalesOrderApiResponse response) {

        // Existing order numbers ek hi query mein (pehle har order ke liye findByOrderNumber)
        Set<String> existingOrderNumbers =
                new HashSet<>(orderRepository.findAllOrderNumbers());

        List<JdeSalesOrder> newOrders = new ArrayList<>();
        List<JdeSalesOrderLine> newLines = new ArrayList<>();

        for (JdeSalesOrderResponseDTO dto : response.getOrders()) {

            if (existingOrderNumbers.contains(dto.getOrderNumber())) {
                continue;
            }

            JdeSalesOrder order = new JdeSalesOrder();

            order.setOrderNumber(
                    dto.getOrderNumber() != null
                            ? dto.getOrderNumber().trim()
                            : null);

            order.setOrderType(
                    dto.getOrderType() != null
                            ? dto.getOrderType().trim()
                            : null);

            order.setCompany(
                    dto.getCompany() != null
                            ? dto.getCompany().trim()
                            : null);

            order.setCustomerNumber(
                    dto.getCustomerNumber() != null
                            ? dto.getCustomerNumber().trim()
                            : null);

            order.setBranchPlant(
                    dto.getBranchPlant() != null
                            ? dto.getBranchPlant().trim()
                            : null);

            order.setStatus(
                    dto.getStatus() != null
                            ? dto.getStatus().trim()
                            : null);

            order.setCurrencyCode(
                    dto.getCurrencyCode() != null
                            ? dto.getCurrencyCode().trim()
                            : null);

            order.setShipToCustomerNumber(
                    dto.getShipToCustomerNumber());

            order.setShipToCustomerName(
                    dto.getShipToCustomerName() != null
                            ? dto.getShipToCustomerName().trim()
                            : null);

            order.setShipToAddress1(
                    dto.getShipToAddress1() != null
                            ? dto.getShipToAddress1().trim()
                            : null);

            order.setShipToCity(
                    dto.getShipToCity() != null
                            ? dto.getShipToCity().trim()
                            : null);

            order.setShipToState(
                    dto.getShipToState() != null
                            ? dto.getShipToState().trim()
                            : null);

            order.setShipToPinCode(
                    dto.getShipToPinCode() != null
                            ? dto.getShipToPinCode().trim()
                            : null);

            // Weight & Volume
            order.setTotalWeight(dto.getTotalWeight());

            order.setWeightUnit(
                    dto.getWeightUnit() != null
                            ? dto.getWeightUnit().trim()
                            : null);

            order.setTotalVolume(dto.getTotalVolume());

            order.setVolumeUnit(
                    dto.getVolumeUnit() != null
                            ? dto.getVolumeUnit().trim()
                            : null);

            if (dto.getOrderDate() != null &&
                    !dto.getOrderDate().isBlank()) {

                order.setOrderDate(
                        LocalDate.parse(dto.getOrderDate())
                );
            }

            if (dto.getRequestedDate() != null &&
                    !dto.getRequestedDate().isBlank()) {

                order.setRequestedDate(
                        LocalDate.parse(dto.getRequestedDate())
                );
            }

            newOrders.add(order);

            // Isi sync mein dobara aaye same order number ko bhi skip karna hai
            existingOrderNumbers.add(order.getOrderNumber());

            if (dto.getLines() != null) {

                for (JdeSalesOrderLineDTO lineDto : dto.getLines()) {

                    JdeSalesOrderLine line =
                            new JdeSalesOrderLine();

                    line.setSalesOrder(order);
                    line.setLineNumber(lineDto.getLineNumber());
                    line.setItemNumber(lineDto.getItemNumber());
                    line.setDescription(lineDto.getDescription());
                    line.setQuantity(lineDto.getQuantity());
                    line.setUom(lineDto.getUom());
                    line.setUnitPrice(lineDto.getUnitPrice());
                    line.setLineAmount(lineDto.getLineAmount());

                    newLines.add(line);
                }
            }
        }

        // Orders pehle, phir lines (lines order ko reference karti hain)
        orderRepository.saveAll(newOrders);
        lineRepository.saveAll(newLines);
    }
}