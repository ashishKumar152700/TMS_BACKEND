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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class JdeSalesOrderSyncServiceImpl
        implements JdeSalesOrderSyncService {

    private final JdeSalesOrderClient jdeSalesOrderClient;
    private final JdeSalesOrderRepository orderRepository;
    private final JdeSalesOrderLineRepository lineRepository;

    @Override
    @Transactional
    public void syncSalesOrders() {

        JdeSalesOrderApiResponse response =
                jdeSalesOrderClient.getSalesOrders();

        if (response == null || response.getOrders() == null) {
            return;
        }

        for (JdeSalesOrderResponseDTO dto : response.getOrders()) {

            if (orderRepository.findByOrderNumber(dto.getOrderNumber()).isPresent()) {
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

            order = orderRepository.save(order);

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

                    lineRepository.save(line);
                }
            }
        }

        log.info("Sales Orders Sync Completed");
    }
}