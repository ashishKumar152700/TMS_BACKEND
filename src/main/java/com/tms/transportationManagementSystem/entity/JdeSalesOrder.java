package com.tms.transportationManagementSystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "jde_sales_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JdeSalesOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String orderNumber;

    private String orderType;
    private String company;
    private String customerNumber;

    private LocalDate orderDate;
    private LocalDate requestedDate;

    private String branchPlant;
    private String status;
    private String currencyCode;

    private Long shipToCustomerNumber;
    private String shipToCustomerName;
    private String shipToAddress1;
    private String shipToCity;
    private String shipToState;
    private String shipToPinCode;

    @Column(precision = 15, scale = 3)
    private BigDecimal totalWeight;

    private String weightUnit;

    @Column(precision = 15, scale = 3)
    private BigDecimal totalVolume;

    private String volumeUnit;

    @OneToMany(
            mappedBy = "salesOrder",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    private List<JdeSalesOrderLine> lines;
}