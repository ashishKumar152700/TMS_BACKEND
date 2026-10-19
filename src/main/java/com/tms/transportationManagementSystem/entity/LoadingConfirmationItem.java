package com.tms.transportationManagementSystem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "loading_confirmation_items")
@Getter
@Setter
public class LoadingConfirmationItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "loading_confirmation_id", nullable = false)
    private LoadingConfirmation loadingConfirmation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sales_order_line_id")
    private JdeSalesOrderLine salesOrderLine;

    private Long salesOrderId;
    private String orderNumber;
    private String itemNumber;

    @Column(length = 500)
    private String itemDescription;

    private String uom;
    private Double orderedQuantity;
    private Double loadedQuantity;
    private Double damagedQuantity;
    private Double rejectedQuantity;
    private Double shortQuantity;

    @Column(length = 1000)
    private String damageRemarks;

    @Column(length = 1000)
    private String remarks;
}
