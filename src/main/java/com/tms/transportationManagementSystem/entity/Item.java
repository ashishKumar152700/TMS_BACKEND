package com.tms.transportationManagementSystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_number")
    private String itemNumber;

    @Column(name = "description")
    private String description;

    @Column(name = "description_2")
    private String description2;

    @Column(name = "line_type")
    private String lineType;

    @Column(name = "stocking_type")
    private String stockingType;

    @Column(name = "third_item_number")
    private String thirdItemNumber;

    @Column(name = "short_item_no")
    private Long shortItemNo;
}