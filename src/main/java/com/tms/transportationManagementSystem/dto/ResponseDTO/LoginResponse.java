package com.tms.transportationManagementSystem.dto.ResponseDTO;

import com.tms.transportationManagementSystem.entity.User;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    private String token;

    private Long userId;
    private String email;
    private String name;
    private Set<String> roles;
}