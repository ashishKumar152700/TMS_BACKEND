package com.tms.transportationManagementSystem.controller;

import com.tms.transportationManagementSystem.security.JwtUtil;
import com.tms.transportationManagementSystem.dto.RequestDTO.LoginRequest;
import com.tms.transportationManagementSystem.dto.ResponseDTO.LoginResponse;
import com.tms.transportationManagementSystem.entity.User;
import com.tms.transportationManagementSystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import com.tms.transportationManagementSystem.entity.Role;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new UsernameNotFoundException("Invalid Email"+request.getEmail()));

        boolean isMatch = passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        );

        if (!isMatch) {
            throw new RuntimeException("Invalid Password");
        }

        String token = jwtUtil.generateTokenFromMail(user.getEmail());

        Set<String> roleNames = user.getRoles()
                .stream()
                .map(Role::getRoleName)
                .collect(Collectors.toSet());

        return LoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .roles(roleNames)
                .build();
    }
}