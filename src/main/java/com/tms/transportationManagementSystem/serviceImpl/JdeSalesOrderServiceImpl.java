package com.tms.transportationManagementSystem.serviceImpl;

import com.tms.transportationManagementSystem.dto.ResponseDTO.JdeSalesOrderLineDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.JdeSalesOrderResponseDTO;
import com.tms.transportationManagementSystem.entity.JdeSalesOrder;
import com.tms.transportationManagementSystem.repository.JdeSalesOrderLineRepository;
import com.tms.transportationManagementSystem.repository.JdeSalesOrderRepository;
import com.tms.transportationManagementSystem.service.JdeSalesOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JdeSalesOrderServiceImpl implements JdeSalesOrderService {

    private final JdeSalesOrderRepository orderRepository;
    private final JdeSalesOrderLineRepository lineRepository;

    @Override
    public List<JdeSalesOrderResponseDTO> getAllSalesOrders() {

        List<JdeSalesOrder> orders = orderRepository.findAll();

        return orders.stream().map(order -> {

            JdeSalesOrderResponseDTO dto = new JdeSalesOrderResponseDTO();

            dto.setId(order.getId());
            dto.setOrderNumber(order.getOrderNumber());
            dto.setOrderType(order.getOrderType());
            dto.setCompany(order.getCompany());
            dto.setCustomerNumber(order.getCustomerNumber());

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

            dto.setBranchPlant(order.getBranchPlant());
            dto.setStatus(order.getStatus());
            dto.setCurrencyCode(order.getCurrencyCode());

            dto.setShipToCustomerNumber(order.getShipToCustomerNumber());
            dto.setShipToCustomerName(order.getShipToCustomerName());
            dto.setShipToAddress1(order.getShipToAddress1());
            dto.setShipToCity(order.getShipToCity());
            dto.setShipToState(order.getShipToState());
            dto.setShipToPinCode(order.getShipToPinCode());

            List<JdeSalesOrderLineDTO> lineDtos =
                    lineRepository.findBySalesOrder(order)
                            .stream()
                            .map(line -> {

                                JdeSalesOrderLineDTO lineDto =
                                        new JdeSalesOrderLineDTO();

                                lineDto.setLineNumber(line.getLineNumber());
                                lineDto.setItemNumber(line.getItemNumber());
                                lineDto.setDescription(line.getDescription());
                                lineDto.setQuantity(line.getQuantity());
                                lineDto.setUom(line.getUom());
                                lineDto.setUnitPrice(line.getUnitPrice());
                                lineDto.setLineAmount(line.getLineAmount());

                                return lineDto;
                            })
                            .collect(Collectors.toList());

            dto.setLines(lineDtos);

            return dto;

        }).collect(Collectors.toList());
    }
}