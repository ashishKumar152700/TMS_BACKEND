package com.tms.transportationManagementSystem.serviceImpl;

import com.tms.transportationManagementSystem.dto.ResponseDTO.*;
import com.tms.transportationManagementSystem.entity.JdeSalesOrder;
import com.tms.transportationManagementSystem.entity.Route;
import com.tms.transportationManagementSystem.entity.RouteStop;
import com.tms.transportationManagementSystem.repository.JdeSalesOrderLineRepository;
import com.tms.transportationManagementSystem.repository.JdeSalesOrderRepository;
import com.tms.transportationManagementSystem.repository.RouteRepository;
import com.tms.transportationManagementSystem.repository.RouteStopRepository;
import com.tms.transportationManagementSystem.service.JdeSalesOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JdeSalesOrderServiceImpl implements JdeSalesOrderService {

    private final JdeSalesOrderRepository orderRepository;
    private final JdeSalesOrderLineRepository lineRepository;
    private final RouteRepository routeRepository;
    private final RouteStopRepository routeStopRepository;

    @Override
    public OrderRouteGroupingResponseDTO groupOrdersByRoute() {

        List<JdeSalesOrder> orders = orderRepository.findAll();

        Map<Long, List<OrderSummaryDTO>> routeWiseOrders = new HashMap<>();

        List<UnmappedOrderDTO> unmappedOrders =
                new ArrayList<>();

        for (JdeSalesOrder order : orders) {

            String city = order.getShipToCity();

            if (city == null || city.trim().isEmpty()) {

                unmappedOrders.add(
                        UnmappedOrderDTO.builder()
                                .orderId(order.getId())
                                .orderNumber(order.getOrderNumber())
                                .city(null)
                                .customerNumber(order.getCustomerNumber())
                                .reason("City is blank")
                                .build()
                );

                continue;
            }

            Long routeId = null;

            List<RouteStop> stops =
                    routeStopRepository.findByStopNameIgnoreCase(city);

            if (!stops.isEmpty()) {

                RouteStop bestStop = stops.stream()
                        .filter(stop -> stop.getSequenceNo() != null)
                        .min(
                                Comparator.comparing(RouteStop::getSequenceNo)
                                        .thenComparing(RouteStop::getDistanceFromSource)
                        )
                        .orElse(stops.get(0));

                routeId = bestStop.getRoute().getId();
            }
            if (routeId == null) {

                List<Route> routes =
                        routeRepository.findByDestinationLocationIgnoreCase(city);

                if (!routes.isEmpty()) {
                    routeId = routes.get(0).getId();
                }
            }

            if (routeId == null) {

                unmappedOrders.add(
                        UnmappedOrderDTO.builder()
                                .orderId(order.getId())
                                .orderNumber(order.getOrderNumber())
                                .city(order.getShipToCity())
                                .customerNumber(order.getCustomerNumber())
                                .reason("No route found")
                                .build()
                );

                continue;
            }

            routeWiseOrders
                    .computeIfAbsent(routeId, k -> new ArrayList<>())
                    .add(
                            OrderSummaryDTO.builder()
                                    .orderId(order.getId())
                                    .orderNumber(order.getOrderNumber())
                                    .city(order.getShipToCity())
                                    .customerNumber(order.getCustomerNumber())
                                    .build()
                    );
        }

        List<RouteOrderGroupResponseDTO> mappedRoutes =
                new ArrayList<>();

        for (Map.Entry<Long, List<OrderSummaryDTO>> entry :
                routeWiseOrders.entrySet()) {

            Route route = routeRepository
                    .findById(entry.getKey())
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Route not found : " + entry.getKey()));

            mappedRoutes.add(
                    RouteOrderGroupResponseDTO.builder()
                            .routeId(route.getId())
                            .routeCode(route.getRouteCode())
                            .sourceLocation(route.getSourceLocation())
                            .destinationLocation(route.getDestinationLocation())
                            .orders(entry.getValue())
                            .build()
            );
        }

        return OrderRouteGroupingResponseDTO.builder()
                .mappedRoutes(mappedRoutes)
                .unmappedOrders(unmappedOrders)
                .build();
    }

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