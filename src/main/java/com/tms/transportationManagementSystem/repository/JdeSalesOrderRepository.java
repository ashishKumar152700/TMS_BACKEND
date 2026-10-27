
package com.tms.transportationManagementSystem.repository;

import com.tms.transportationManagementSystem.entity.JdeSalesOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JdeSalesOrderRepository
        extends JpaRepository<JdeSalesOrder, Long> {

    Optional<JdeSalesOrder> findByOrderNumber(String orderNumber);

    List<JdeSalesOrder> findByCustomerNumber(String customerNumber);

    long countByShipmentId(Long shipmentId);

    List<JdeSalesOrder> findByShipment_Id(Long shipmentId);

    @Query(value = """
            SELECT j.*
            FROM public.shipment_orders so
            JOIN public.jde_sales_orders j
                ON j.id = so.order_id
            WHERE so.shipment_id = :shipmentId
            ORDER BY so.id ASC
            """, nativeQuery = true)
    List<JdeSalesOrder> findSalesOrdersByShipmentId(
            @Param("shipmentId") Long shipmentId
    );
}
