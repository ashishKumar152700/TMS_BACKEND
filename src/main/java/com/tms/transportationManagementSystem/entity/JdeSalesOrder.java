package com.tms.transportationManagementSystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @OneToMany(mappedBy = "salesOrder",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    private List<JdeSalesOrderLine> lines;
}