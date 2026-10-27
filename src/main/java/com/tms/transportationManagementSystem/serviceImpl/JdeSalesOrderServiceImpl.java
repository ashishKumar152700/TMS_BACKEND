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

    private static final String REASON_CITY_BLANK = "City is blank";
    private static final String REASON_NO_ROUTE = "No route found";

    private final JdeSalesOrderRepository orderRepository;
    private final RouteRepository routeRepository;
    private final RouteStopRepository routeStopRepository;
    private final OrderRouteMappingRepository mappingRepository;
    private final VehicleRepository vehicleRepository;
    private final SalesOrderLookup salesOrderLookup;

    // Ek order ka resolved route. route == null ho to unmappedReason set hota hai.
    private record OrderRoute(
            JdeSalesOrder order,
            Route route,
            String unmappedReason
    ) {
    }


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

        Map<Long, List<JdeSalesOrderLine>> linesByOrderId =
                loadLines(orders);

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
                    .add(convertToDTO(order, linesByOrderId));
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

        // City wale NEW orders ROUTE_ASSIGNED ho jate hain (pehle jaisa hi),
        // ab ek-ek save ki jagah ek saveAll.
        List<JdeSalesOrder> statusChangedOrders = new ArrayList<>();

        for (JdeSalesOrder order : orders) {

            if (!isCityBlank(order.getShipToCity())
                    && order.getOrderStatus() == OrderStatus.NEW) {

                order.setOrderStatus(OrderStatus.ROUTE_ASSIGNED);
                statusChangedOrders.add(order);
            }
        }

        if (!statusChangedOrders.isEmpty()) {
            orderRepository.saveAll(statusChangedOrders);
        }

        Map<Long, List<JdeSalesOrderLine>> linesByOrderId =
                loadLines(orders);

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

        Map<Long, List<RouteStopResponseDTO>>
                routeStopsMap = buildRouteStopsMap();

        for (OrderRoute orderRoute : resolveRoutes(orders)) {

            JdeSalesOrder order = orderRoute.order();

            JdeSalesOrderResponseDTO orderDTO =
                    convertToDTO(order, linesByOrderId);

            if (orderRoute.route() == null) {

                unmappedOrders.add(
                        toUnmappedDTO(order, orderRoute.unmappedReason(), orderDTO)
                );

                continue;
            }

            Route route = orderRoute.route();

            routeCache.put(
                    route.getId(),
                    route
            );

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
                                            .map(routeEntry ->
                                                    toRouteGroupDTO(
                                                            routeCache.get(routeEntry.getKey()),
                                                            routeStopsMap,
                                                            routeEntry.getValue()
                                                    ))
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

        List<Route> uniqueRoutes = findCandidateRoutes(city);

        // Routes already having orders.
        // Pehle poora groupOrdersByRoute() (saare DTOs + lines) banta tha sirf IDs ke liye;
        // ab wahi route resolution logic bina DTO banaye chalta hai.
        Set<Long> activeRouteIds =
                resolveRoutes(orderRepository.findAll())
                        .stream()
                        .map(OrderRoute::route)
                        .filter(Objects::nonNull)
                        .map(Route::getId)
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

        Map<Long, List<JdeSalesOrderLine>> linesByOrderId =
                loadLines(orders);

        Map<Long, List<JdeSalesOrderResponseDTO>> routeWiseOrders =
                new LinkedHashMap<>();

        Map<Long, Route> routeCache =
                new HashMap<>();

        List<UnmappedOrderDTO> unmappedOrders =
                new ArrayList<>();

        // Route Stops Cache
        Map<Long, List<RouteStopResponseDTO>> routeStopsMap =
                buildRouteStopsMap();

        for (OrderRoute orderRoute : resolveRoutes(orders)) {

            JdeSalesOrder order = orderRoute.order();

            if (orderRoute.route() == null) {

                unmappedOrders.add(
                        toUnmappedDTO(
                                order,
                                orderRoute.unmappedReason(),
                                convertToDTO(order, linesByOrderId)
                        )
                );

                continue;
            }

            Route route = orderRoute.route();

            routeCache.put(route.getId(), route);

            routeWiseOrders
                    .computeIfAbsent(
                            route.getId(),
                            k -> new ArrayList<>()
                    )
                    .add(convertToDTO(order, linesByOrderId));
        }

        List<RouteOrderGroupResponseDTO> mappedRoutes =
                routeWiseOrders.entrySet()
                        .stream()
                        .map(entry ->
                                toRouteGroupDTO(
                                        routeCache.get(entry.getKey()),
                                        routeStopsMap,
                                        entry.getValue()
                                ))
                        .collect(Collectors.toList());

        return OrderRouteGroupingResponseDTO.builder()
                .mappedRoutes(mappedRoutes)
                .unmappedOrders(unmappedOrders)
                .build();
    }


    /**
     * Har order ka route decide karta hai: manual mapping ho to wahi,
     * warna best matching route (jis route pe pehle se zyada orders hain).
     * Mappings aur city-wise candidate routes ek baar load/cache hote hain
     * (pehle har order ke liye 3 queries jati thi).
     */
    private List<OrderRoute> resolveRoutes(List<JdeSalesOrder> orders) {

        Map<Long, OrderRouteMapping> mappingByOrderId =
                mappingRepository.findAll()
                        .stream()
                        .filter(mapping -> mapping.getOrder() != null)
                        .collect(Collectors.toMap(
                                mapping -> mapping.getOrder().getId(),
                                mapping -> mapping,
                                (first, second) -> first
                        ));

        Map<String, List<Route>> candidateRoutesByCity =
                new HashMap<>();

        Map<Long, Integer> routeOrderCounts =
                new HashMap<>();

        List<OrderRoute> result =
                new ArrayList<>(orders.size());

        for (JdeSalesOrder order : orders) {

            String city = order.getShipToCity();

            if (isCityBlank(city)) {
                result.add(new OrderRoute(order, null, REASON_CITY_BLANK));
                continue;
            }

            OrderRouteMapping manualMapping =
                    mappingByOrderId.get(order.getId());

            Route route;

            if (manualMapping != null) {

                route = manualMapping.getRoute();

            } else {

                route = findBestRoute(
                        candidateRoutesByCity.computeIfAbsent(
                                city,
                                this::findCandidateRoutes
                        ),
                        routeOrderCounts
                );
            }

            if (route == null) {
                result.add(new OrderRoute(order, null, REASON_NO_ROUTE));
                continue;
            }

            routeOrderCounts.merge(route.getId(), 1, Integer::sum);

            result.add(new OrderRoute(order, route, null));
        }

        return result;
    }


    /**
     * Destination ya stop se match hone wale unique routes.
     */
    private List<Route> findCandidateRoutes(String city) {

        List<Route> candidateRoutes = new ArrayList<>();

        // Destination matches
        candidateRoutes.addAll(
                routeRepository
                        .findByDestinationLocationIgnoreCase(city)
        );

        // Stop matches
        candidateRoutes.addAll(
                routeStopRepository
                        .findByStopNameIgnoreCase(city)
                        .stream()
                        .map(RouteStop::getRoute)
                        .toList()
        );

        // Remove duplicate routes
        return new ArrayList<>(
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
    }


    private Route findBestRoute(
            List<Route> candidateRoutes,
            Map<Long, Integer> routeOrderCounts
    ) {

        if (candidateRoutes.isEmpty()) {
            return null;
        }

        // Prefer route already having more orders
        return candidateRoutes.stream()
                .max(
                        Comparator.comparingInt(
                                route ->
                                        routeOrderCounts.getOrDefault(
                                                route.getId(),
                                                0
                                        )
                        )
                )
                .orElse(candidateRoutes.get(0));
    }


    private static boolean isCityBlank(String city) {
        return city == null || city.trim().isEmpty();
    }


    private Map<Long, List<RouteStopResponseDTO>> buildRouteStopsMap() {

        return routeStopRepository.findAll()
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
    }


    private RouteOrderGroupResponseDTO toRouteGroupDTO(
            Route route,
            Map<Long, List<RouteStopResponseDTO>> routeStopsMap,
            List<JdeSalesOrderResponseDTO> orders
    ) {

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
                .orders(orders)
                .build();
    }


    private UnmappedOrderDTO toUnmappedDTO(
            JdeSalesOrder order,
            String reason,
            JdeSalesOrderResponseDTO orderDTO
    ) {

        return UnmappedOrderDTO.builder()
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .customerNumber(order.getCustomerNumber())
                .city(order.getShipToCity())
                .reason(reason)
                .order(orderDTO)
                .build();
    }


    @Override
    public List<JdeSalesOrderResponseDTO> getAllSalesOrders() {

        List<JdeSalesOrder> orders = orderRepository.findAll();

        Map<Long, List<JdeSalesOrderLine>> linesByOrderId =
                loadLines(orders);

        return orders.stream()
                .map(order -> convertToDTO(order, linesByOrderId))
                .collect(Collectors.toList());
    }


    // Saare orders ki lines ek saath (har order ke liye alag query nahi)
    private Map<Long, List<JdeSalesOrderLine>> loadLines(
            List<JdeSalesOrder> orders
    ) {

        return salesOrderLookup.linesByOrderId(
                orders.stream()
                        .map(JdeSalesOrder::getId)
                        .toList()
        );
    }

    private JdeSalesOrderResponseDTO convertToDTO(
            JdeSalesOrder order,
            Map<Long, List<JdeSalesOrderLine>> linesByOrderId
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
                linesByOrderId.getOrDefault(order.getId(), List.of())
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
