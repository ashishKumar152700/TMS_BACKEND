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

        Map<Long, List<JdeSalesOrderResponseDTO>> routeWiseOrders =
                new LinkedHashMap<>();

        Map<Long, Route> routeCache =
                new HashMap<>();

        List<UnmappedOrderDTO> unmappedOrders =
                new ArrayList<>();

        // Route Stops Cache
        Map<Long, List<RouteStopResponseDTO>> routeStopsMap =
                routeStopRepository.findAll()
                        .stream()
                        .collect(Collectors.groupingBy(
                                stop -> stop.getRoute().getId(),
                                Collectors.mapping(
                                        stop -> RouteStopResponseDTO.builder()
                                                .id(stop.getId())
                                                .stopName(stop.getStopName())
                                                .distanceFromSource(
                                                        stop.getDistanceFromSource()
                                                )
                                                .routeId(
                                                        stop.getRoute().getId()
                                                )
                                                .routeCode(
                                                        stop.getRoute().getRouteCode()
                                                )
                                                .build(),
                                        Collectors.toList()
                                )
                        ));

        for (JdeSalesOrder order : orders) {

            String city = order.getShipToCity();

            if (city == null || city.trim().isEmpty()) {

                unmappedOrders.add(
                        UnmappedOrderDTO.builder()
                                .reason("City is blank")
                                .order(convertToDTO(order))
                                .build()
                );

                continue;
            }

            Route route = findBestRoute(
                    city,
                    routeWiseOrders
            );

            if (route == null) {

                unmappedOrders.add(
                        UnmappedOrderDTO.builder()
                                .reason("No route found")
                                .order(convertToDTO(order))
                                .build()
                );

                continue;
            }

            routeCache.put(route.getId(), route);

            routeWiseOrders
                    .computeIfAbsent(
                            route.getId(),
                            k -> new ArrayList<>()
                    )
                    .add(convertToDTO(order));
        }

        List<RouteOrderGroupResponseDTO> mappedRoutes =
                routeWiseOrders.entrySet()
                        .stream()
                        .map(entry -> {

                            Route route =
                                    routeCache.get(entry.getKey());

                            return RouteOrderGroupResponseDTO.builder()
                                    .routeId(route.getId())
                                    .routeCode(route.getRouteCode())
                                    .sourceLocation(
                                            route.getSourceLocation()
                                    )
                                    .destinationLocation(
                                            route.getDestinationLocation()
                                    )
                                    .routeStops(
                                            routeStopsMap.getOrDefault(
                                                    route.getId(),
                                                    Collections.emptyList()
                                            )
                                    )
                                    .orders(entry.getValue())
                                    .build();
                        })
                        .collect(Collectors.toList());

        return OrderRouteGroupingResponseDTO.builder()
                .mappedRoutes(mappedRoutes)
                .unmappedOrders(unmappedOrders)
                .build();
    }


    private Route findBestRoute(
            String city,
            Map<Long, List<JdeSalesOrderResponseDTO>> routeWiseOrders
    ) {

        List<Route> candidateRoutes = new ArrayList<>();

        // Destination matches
        candidateRoutes.addAll(
                routeRepository
                        .findByDestinationLocationIgnoreCase(city)
        );

        // Stop matches
        List<RouteStop> stops =
                routeStopRepository
                        .findByStopNameIgnoreCase(city);

        candidateRoutes.addAll(
                stops.stream()
                        .map(RouteStop::getRoute)
                        .collect(Collectors.toList())
        );

        // Remove duplicate routes
        candidateRoutes =
                new ArrayList<>(
                        candidateRoutes.stream()
                                .collect(
                                        Collectors.toMap(
                                                Route::getId,
                                                route -> route,
                                                (r1, r2) -> r1
                                        )
                                )
                                .values()
                );

        if (candidateRoutes.isEmpty()) {
            return null;
        }

        // Prefer route already having more orders
        return candidateRoutes.stream()
                .max(
                        Comparator.comparingInt(
                                route ->
                                        routeWiseOrders
                                                .getOrDefault(
                                                        route.getId(),
                                                        Collections.emptyList()
                                                )
                                                .size()
                        )
                )
                .orElse(candidateRoutes.get(0));
    }


    @Override
    public List<JdeSalesOrderResponseDTO> getAllSalesOrders() {

        return orderRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private JdeSalesOrderResponseDTO convertToDTO(
            JdeSalesOrder order
    ) {

        JdeSalesOrderResponseDTO dto =
                new JdeSalesOrderResponseDTO();

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

        dto.setShipToCustomerNumber(
                order.getShipToCustomerNumber()
        );

        dto.setShipToCustomerName(
                order.getShipToCustomerName()
        );

        dto.setShipToAddress1(
                order.getShipToAddress1()
        );

        dto.setShipToCity(
                order.getShipToCity()
        );

        dto.setShipToState(
                order.getShipToState()
        );

        dto.setShipToPinCode(
                order.getShipToPinCode()
        );

        List<JdeSalesOrderLineDTO> lineDtos =
                lineRepository.findBySalesOrder(order)
                        .stream()
                        .map(line -> {

                            JdeSalesOrderLineDTO lineDto =
                                    new JdeSalesOrderLineDTO();

                            lineDto.setLineNumber(
                                    line.getLineNumber()
                            );

                            lineDto.setItemNumber(
                                    line.getItemNumber()
                            );

                            lineDto.setDescription(
                                    line.getDescription()
                            );

                            lineDto.setQuantity(
                                    line.getQuantity()
                            );

                            lineDto.setUom(
                                    line.getUom()
                            );

                            lineDto.setUnitPrice(
                                    line.getUnitPrice()
                            );

                            lineDto.setLineAmount(
                                    line.getLineAmount()
                            );

                            return lineDto;
                        })
                        .collect(Collectors.toList());

        dto.setLines(lineDtos);

        return dto;
    }
}