package com.tms.transportationManagementSystem.serviceImpl;

import com.tms.transportationManagementSystem.dto.RequestDTO.AssignRouteRequestDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.*;
import com.tms.transportationManagementSystem.entity.*;
import com.tms.transportationManagementSystem.enums.OrderStatus;
import com.tms.transportationManagementSystem.repository.*;
import com.tms.transportationManagementSystem.service.JdeSalesOrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JdeSalesOrderServiceImpl implements JdeSalesOrderService {

    private final JdeSalesOrderRepository orderRepository;
    private final JdeSalesOrderLineRepository lineRepository;
    private final RouteRepository routeRepository;
    private final RouteStopRepository routeStopRepository;
    private final OrderRouteMappingRepository mappingRepository;
    private final VehicleRepository vehicleRepository;



    @Override
    public VehicleSuggestionResponseDTO suggestVehicle(Long orderId) {

        JdeSalesOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        double orderWeight = order.getTotalWeight().doubleValue();
        double orderVolume = order.getTotalVolume().doubleValue();

        List<Vehicle> vehicles = vehicleRepository.findAll();

        Vehicle bestVehicle = vehicles.stream()
                .filter(v ->
                        v.getMaxWeightCapacity() >= orderWeight
                                &&
                                v.getMaxVolumeCapacity() >= orderVolume
                )
                .min(Comparator.comparingDouble(v -> {

                    double remainingVolume =
                            v.getMaxVolumeCapacity() - orderVolume;

                    double remainingWeight =
                            v.getMaxWeightCapacity() - orderWeight;

                    // Volume ko highest priority
                    return remainingVolume + (remainingWeight / 1000.0);

                }))
                .orElseThrow(() ->
                        new RuntimeException("No suitable vehicle found"));

        double weightUtilization =
                (orderWeight / bestVehicle.getMaxWeightCapacity()) * 100;

        double volumeUtilization =
                (orderVolume / bestVehicle.getMaxVolumeCapacity()) * 100;

        return VehicleSuggestionResponseDTO.builder()
                .availableInFleet(true)
                .recommendedVehicleType(bestVehicle.getVehicleType())
                .maxWeightCapacity(bestVehicle.getMaxWeightCapacity())
                .maxVolumeCapacity(bestVehicle.getMaxVolumeCapacity())
                .orderWeight(orderWeight)
                .orderVolume(orderVolume)
                .reason(String.format(
                        "Weight Utilization %.2f%% | Volume Utilization %.2f%%",
                        weightUtilization,
                        volumeUtilization
                ))
                .build();
    }

    @Override
    public CustomerOrderGroupingResponseDTO groupOrdersByCustomer() {

        List<JdeSalesOrder> orders = orderRepository.findAll();

        Map<String, List<JdeSalesOrderResponseDTO>> customerOrders =
                new LinkedHashMap<>();

        Map<String, String> customerNames =
                new HashMap<>();

        for (JdeSalesOrder order : orders) {

            String customerNumber = order.getCustomerNumber();

            if (customerNumber == null || customerNumber.isBlank()) {
                continue;
            }

            customerNames.put(
                    customerNumber,
                    order.getShipToCustomerName()
            );

            customerOrders
                    .computeIfAbsent(
                            customerNumber,
                            k -> new ArrayList<>()
                    )
                    .add(convertToDTO(order));
        }

        List<CustomerOrderGroupResponseDTO> customers =
                customerOrders.entrySet()
                        .stream()
                        .map(entry ->
                                CustomerOrderGroupResponseDTO.builder()
                                        .customerNumber(entry.getKey())
                                        .customerName(customerNames.get(entry.getKey()))
                                        .orders(entry.getValue())
                                        .build()
                        )
                        .toList();

        return CustomerOrderGroupingResponseDTO.builder()
                .customers(customers)
                .build();
    }

    @Override
    public CustomerRouteGroupingResponseDTO
    groupOrdersByCustomerAndRoute() {

        List<JdeSalesOrder> orders =
                orderRepository.findAll();

        Map<String,
                Map<Long,
                        List<JdeSalesOrderResponseDTO>>>
                customerRouteOrders =
                new LinkedHashMap<>();

        Map<String, String> customerNames =
                new HashMap<>();

        Map<Long, Route> routeCache =
                new HashMap<>();

        List<UnmappedOrderDTO> unmappedOrders =
                new ArrayList<>();

        Map<Long, List<JdeSalesOrderResponseDTO>>
                routeWiseOrders =
                new HashMap<>();

        Map<Long, List<RouteStopResponseDTO>>
                routeStopsMap =
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

            String city =
                    order.getShipToCity();

            if (city == null ||
                    city.trim().isEmpty()) {

                unmappedOrders.add(
                        UnmappedOrderDTO.builder()
                                .orderId(order.getId())
                                .orderNumber(order.getOrderNumber())
                                .customerNumber(order.getCustomerNumber())
                                .city(order.getShipToCity())
                                .reason("City is blank")
                                .order(convertToDTO(order))
                                .build()
                );

                continue;
            }

            if (order.getOrderStatus() == OrderStatus.NEW) {
                order.setOrderStatus(OrderStatus.ROUTE_ASSIGNED);
                orderRepository.save(order);
            }


            OrderRouteMapping manualMapping =
                    mappingRepository
                            .findByOrderId(order.getId())
                            .orElse(null);

            Route route;

            if (manualMapping != null) {

                route = manualMapping.getRoute();

            } else {

                route = findBestRoute(
                        city,
                        routeWiseOrders
                );
            }

            if (route == null) {

                unmappedOrders.add(
                        UnmappedOrderDTO.builder()
                                .orderId(order.getId())
                                .orderNumber(order.getOrderNumber())
                                .customerNumber(order.getCustomerNumber())
                                .city(order.getShipToCity())
                                .reason("No route found")
                                .order(convertToDTO(order))
                                .build()
                );

                continue;
            }

            routeCache.put(
                    route.getId(),
                    route
            );

            JdeSalesOrderResponseDTO orderDTO =
                    convertToDTO(order);

            routeWiseOrders
                    .computeIfAbsent(
                            route.getId(),
                            k -> new ArrayList<>()
                    )
                    .add(orderDTO);

            customerNames.put(
                    order.getCustomerNumber(),
                    order.getShipToCustomerName()
            );

            customerRouteOrders
                    .computeIfAbsent(
                            order.getCustomerNumber(),
                            k -> new LinkedHashMap<>()
                    )
                    .computeIfAbsent(
                            route.getId(),
                            k -> new ArrayList<>()
                    )
                    .add(orderDTO);
        }

        List<CustomerRouteGroupResponseDTO>
                customers =
                customerRouteOrders.entrySet()
                        .stream()
                        .map(customerEntry -> {

                            List<RouteOrderGroupResponseDTO>
                                    routes =
                                    customerEntry.getValue()
                                            .entrySet()
                                            .stream()
                                            .map(routeEntry -> {

                                                Route route =
                                                        routeCache.get(
                                                                routeEntry.getKey()
                                                        );

                                                return RouteOrderGroupResponseDTO
                                                        .builder()
                                                        .routeId(
                                                                route.getId()
                                                        )
                                                        .routeCode(
                                                                route.getRouteCode()
                                                        )
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
                                                        .orders(
                                                                routeEntry.getValue()
                                                        )
                                                        .build();
                                            })
                                            .toList();

                            return CustomerRouteGroupResponseDTO
                                    .builder()
                                    .customerNumber(
                                            customerEntry.getKey()
                                    )
                                    .customerName(
                                            customerNames.get(
                                                    customerEntry.getKey()
                                            )
                                    )
                                    .routes(routes)
                                    .build();
                        })
                        .toList();

        return CustomerRouteGroupingResponseDTO
                .builder()
                .customers(customers)
                .unmappedOrders(unmappedOrders)
                .build();
    }

    @Transactional
    @Override
    public String assignRoute(
            AssignRouteRequestDTO request) {

        JdeSalesOrder order =
                orderRepository
                        .findById(request.getOrderId())
                        .orElseThrow();

        Route route =
                routeRepository
                        .findById(request.getRouteId())
                        .orElseThrow();

        boolean valid =
                route.getDestinationLocation()
                        .equalsIgnoreCase(
                                order.getShipToCity())
                        ||
                        route.getRouteStops()
                                .stream()
                                .anyMatch(stop ->
                                        stop.getStopName()
                                                .equalsIgnoreCase(
                                                        order.getShipToCity()
                                                ));

        if (!valid) {
            throw new RuntimeException(
                    "Route does not cover destination");
        }

        OrderRouteMapping mapping =
                mappingRepository
                        .findByOrderId(
                                request.getOrderId())
                        .orElse(
                                new OrderRouteMapping());

        mapping.setOrder(order);
        mapping.setRoute(route);
        mapping.setAssignmentType("MANUAL");
        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String loggedInUser =
                authentication.getName();

        mapping.setAssignedBy(loggedInUser);
        mapping.setAssignedAt(
                LocalDateTime.now());

        mappingRepository.save(mapping);

        order.setOrderStatus(OrderStatus.ROUTE_ASSIGNED);
        orderRepository.save(order);

        return "Route assigned successfully";
    }

    @Override
    public AvailableRouteAssignmentResponseDTO
    getAvailableRoutes(Long orderId) {

        JdeSalesOrder order =
                orderRepository.findById(orderId)
                        .orElseThrow();

        String city = order.getShipToCity();

        List<Route> routes = new ArrayList<>();

        routes.addAll(
                routeRepository
                        .findByDestinationLocationIgnoreCase(city)
        );

        routes.addAll(
                routeStopRepository
                        .findByStopNameIgnoreCase(city)
                        .stream()
                        .map(RouteStop::getRoute)
                        .toList()
        );

        List<Route> uniqueRoutes =
                new ArrayList<>(
                        routes.stream()
                                .collect(Collectors.toMap(
                                        Route::getId,
                                        r -> r,
                                        (a,b) -> a
                                ))
                                .values()
                );

        // Routes already having orders
        Set<Long> activeRouteIds =
                groupOrdersByRoute()
                        .getMappedRoutes()
                        .stream()
                        .map(RouteOrderGroupResponseDTO::getRouteId)
                        .collect(Collectors.toSet());

        List<AvailableRouteResponseDTO> existingRoutes =
                uniqueRoutes.stream()
                        .filter(route ->
                                activeRouteIds.contains(route.getId()))
                        .map(this::convertRouteDTO)
                        .toList();

        List<AvailableRouteResponseDTO> newRoutes =
                uniqueRoutes.stream()
                        .filter(route ->
                                !activeRouteIds.contains(route.getId()))
                        .map(this::convertRouteDTO)
                        .toList();

        return AvailableRouteAssignmentResponseDTO.builder()
                .existingRoutes(existingRoutes)
                .newRoutes(newRoutes)
                .build();
    }

    private AvailableRouteResponseDTO convertRouteDTO(
            Route route) {

        return AvailableRouteResponseDTO.builder()
                .routeId(route.getId())
                .routeCode(route.getRouteCode())
                .sourceLocation(route.getSourceLocation())
                .destinationLocation(route.getDestinationLocation())
                .build();
    }


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
                                .orderId(order.getId())
                                .orderNumber(order.getOrderNumber())
                                .customerNumber(order.getCustomerNumber())
                                .city(order.getShipToCity())
                                .reason("City is blank")
                                .order(convertToDTO(order))
                                .build()
                );

                continue;
            }

            OrderRouteMapping manualMapping =
                    mappingRepository
                            .findByOrderId(order.getId())
                            .orElse(null);

            Route route;

            if(manualMapping != null)
            {
                route = manualMapping.getRoute();
            }
            else
            {
                route = findBestRoute(
                        city,
                        routeWiseOrders
                );
            }
            if (route == null) {

                unmappedOrders.add(
                        UnmappedOrderDTO.builder()
                                .orderId(order.getId())
                                .orderNumber(order.getOrderNumber())
                                .customerNumber(order.getCustomerNumber())
                                .city(order.getShipToCity())
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

        dto.setTotalWeight(
                order.getTotalWeight()
        );

        dto.setWeightUnit(
                order.getWeightUnit()
        );

        dto.setTotalVolume(
                order.getTotalVolume()
        );

        dto.setVolumeUnit(
                order.getVolumeUnit()
        );
        dto.setOrderStatus(
                order.getOrderStatus()
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