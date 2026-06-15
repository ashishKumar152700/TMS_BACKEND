package com.tms.transportationManagementSystem.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "jde_customers_master")
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

    @Column(name = "gst_number")
    private String gstNumber;

    @Column(name = "contact_person")
    private String contactPerson;

    @Column(name = "mobile")
    private String mobile;

    @Column(name = "email")
    private String email;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "pin_code")
    private String pinCode;

    @Column(name = "country")
    private String country;

    @Column(name = "active")
    private String active;

    @Column(name = "remarks")
    private String remarks;
}