package com.tms.transportationManagementSystem.dto.RequestDTO;

import lombok.Data;

import java.util.Set;

@Data
public class CreateUserRequest {

    private String name;

    private String email;

    private String password;

    private Set<Long> roleIds;

    private Long jdeAddressNumber;

    private String transporterName;
}