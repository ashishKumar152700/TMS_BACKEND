package com.tms.transportationManagementSystem.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
@Entity
@Table(name = "transporters")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transporter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String companyName;
    private String gstNumber;
    private String panNumber;
    private String contactPerson;
    private String mobile;
    private String email;
    private String address;
    private String city;
    private String state;
    private String pincode;

    private String status;

    @Column(unique = true)
    private Long addressNumber;

    private Boolean active;

    @Column(length = 500)
    private String remarks;

    @OneToMany(mappedBy = "transporter",
            cascade = CascadeType.ALL)
    private List<Vehicle> vehicles;

    @OneToMany(mappedBy = "transporter",
            cascade = CascadeType.ALL)
    private List<TransporterRoute> transporterRoutes;
}