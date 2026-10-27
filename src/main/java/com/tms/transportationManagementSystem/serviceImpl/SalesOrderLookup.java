package com.tms.transportationManagementSystem.serviceImpl;

import com.tms.transportationManagementSystem.dto.ResponseDTO.ShipmentOrderResponseDTO;
import com.tms.transportationManagementSystem.entity.JdeSalesOrder;
import com.tms.transportationManagementSystem.entity.JdeSalesOrderLine;
import com.tms.transportationManagementSystem.entity.ShipmentOrder;
import com.tms.transportationManagementSystem.repository.JdeSalesOrderLineRepository;
import com.tms.transportationManagementSystem.repository.JdeSalesOrderRepository;
import com.tms.transportationManagementSystem.repository.ShipmentOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Sales orders / lines ko batch mein load karne ke helpers.
 * Har shipment/order ke liye alag query (N+1) ki jagah chunked IN queries chalti hain.
 */
@Component
@RequiredArgsConstructor
public class SalesOrderLookup {

    // PostgreSQL ek statement mein ~32k parameters tak hi allow karta hai
    private static final int IN_CHUNK_SIZE = 1000;

    private final ShipmentOrderRepository shipmentOrderRepository;
    private final JdeSalesOrderRepository orderRepository;
    private final JdeSalesOrderLineRepository lineRepository;

    /**
     * shipmentId -> us shipment ke sales orders (shipment_orders ke order mein).
     * Jo order DB mein nahi mila wo skip hota hai, jaise pehle findById(..).orElse(null) se hota tha.
     */
    public Map<Long, List<JdeSalesOrder>> ordersByShipmentId(Collection<Long> shipmentIds) {

        Map<Long, List<JdeSalesOrder>> result = new HashMap<>();

        if (shipmentIds.isEmpty()) {
            return result;
        }

        List<ShipmentOrder> shipmentOrders =
                inChunks(shipmentIds, shipmentOrderRepository::findByShipmentIdIn);

        Set<Long> orderIds = shipmentOrders.stream()
                .map(ShipmentOrder::getOrderId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, JdeSalesOrder> ordersById =
                inChunks(orderIds, orderRepository::findAllById)
                        .stream()
                        .collect(Collectors.toMap(
                                JdeSalesOrder::getId,
                                Function.identity(),
                                (a, b) -> a
                        ));

        for (ShipmentOrder shipmentOrder : shipmentOrders) {

            JdeSalesOrder order = ordersById.get(shipmentOrder.getOrderId());

            if (order != null) {
                result.computeIfAbsent(
                                shipmentOrder.getShipmentId(),
                                k -> new ArrayList<>())
                        .add(order);
            }
        }

        return result;
    }

    /**
     * orderId -> us order ki lines.
     */
    public Map<Long, List<JdeSalesOrderLine>> linesByOrderId(Collection<Long> orderIds) {

        if (orderIds.isEmpty()) {
            return new HashMap<>();
        }

        return inChunks(orderIds, lineRepository::findBySalesOrder_IdIn)
                .stream()
                .filter(line -> line.getSalesOrder() != null)
                .collect(Collectors.groupingBy(
                        line -> line.getSalesOrder().getId(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
    }

    public static ShipmentOrderResponseDTO toShipmentOrderDTO(JdeSalesOrder order) {

        return ShipmentOrderResponseDTO.builder()
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .customerNumber(order.getCustomerNumber())
                .customerName(order.getShipToCustomerName())
                .city(order.getShipToCity())
                .state(order.getShipToState())
                .totalWeight(order.getTotalWeight())
                .totalVolume(order.getTotalVolume())
                .orderDate(order.getOrderDate())
                .orderStatus(order.getOrderStatus().name())
                .build();
    }

    public static <T, R> List<R> inChunks(
            Collection<T> values,
            Function<List<T>, List<R>> query
    ) {

        List<T> distinct = new ArrayList<>(new LinkedHashSet<>(values));
        List<R> result = new ArrayList<>();

        for (int i = 0; i < distinct.size(); i += IN_CHUNK_SIZE) {
            result.addAll(query.apply(
                    distinct.subList(i, Math.min(i + IN_CHUNK_SIZE, distinct.size()))
            ));
        }

        return result;
    }
}
