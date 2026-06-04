package com.tms.transportationManagementSystem.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "sales_order_line")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesOrderLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "line_number")
    private Integer lineNumber;

    @Column(name = "item_number")
    private String itemNumber;

    @Column(name = "item_description")
    private String itemDescription;

    @Column(name = "quantity")
    private BigDecimal quantity;

    @Column(name = "uom")
    private String uom;

    @Column(name = "unit_price")
    private BigDecimal unitPrice;

    @Column(name = "line_amount")
    private BigDecimal lineAmount;

    @ManyToOne
    @JoinColumn(name = "sales_order_id")
    private SalesOrder salesOrder;
}