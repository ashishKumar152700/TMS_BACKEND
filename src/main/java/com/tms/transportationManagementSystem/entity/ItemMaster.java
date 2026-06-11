package com.tms.transportationManagementSystem.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "jde_item_master")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String itemNumber;

    private String description;

    private String description2;

    private String lineType;

    private String stockingType;

    private String thirdItemNumber;

    private Long shortItemNo;
}