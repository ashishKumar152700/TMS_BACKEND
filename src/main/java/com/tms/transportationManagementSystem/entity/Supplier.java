package com.tms.transportationManagementSystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "suppliers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "address_number")
    private Long addressNumber;

    @Column(name = "alpha_name")
    private String alphaName;

    @Column(name = "individual_tax_id")
    private String individualTaxId;

    @Column(name = "long_address")
    private String longAddress;

    @Column(name = "industry_class")
    private String industryClass;

    @Column(name = "schedule_type")
    private String scheduleType;

    @Column(name = "tax_id")
    private String taxId;
}