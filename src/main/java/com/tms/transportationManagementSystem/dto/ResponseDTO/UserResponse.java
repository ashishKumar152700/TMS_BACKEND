package com.tms.transportationManagementSystem.dto.ResponseDTO;

import lombok.Data;

import java.util.Set;

@Data
public class UserResponse {

    private Long id;

    private String name;

    private String email;

    private Set<String> roles;
}