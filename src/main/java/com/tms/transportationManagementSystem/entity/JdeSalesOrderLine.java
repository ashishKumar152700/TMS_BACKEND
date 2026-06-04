package com.tms.transportationManagementSystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "jde_sales_order_lines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JdeSalesOrderLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double lineNumber;

    private String itemNumber;

    private String description;

    private Double quantity;

    private String uom;

    private Double unitPrice;

    private Double lineAmount;

    @ManyToOne
    @JoinColumn(name = "sales_order_id")
    private JdeSalesOrder salesOrder;
}