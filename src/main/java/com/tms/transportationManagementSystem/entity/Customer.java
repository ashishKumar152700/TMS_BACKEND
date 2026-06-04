package com.tms.transportationManagementSystem.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "address_number")
    private Long addressNumber;

    @Column(name = "company_code")
    private String companyCode;

    @Column(name = "alpha_name")
    private String alphaName;

    @Column(name = "cm")
    private Integer cm;

    @Column(name = "long_address")
    private String longAddress;

    @Column(name = "industry_class")
    private String industryClass;

    @Column(name = "schedule_type")
    private String scheduleType;

    @Column(name = "individual_tax_id")
    private String individualTaxId;
}