package com.tms.transportationManagementSystem.controller;

import com.tms.transportationManagementSystem.dto.RequestDTO.CreateUserRequest;
import com.tms.transportationManagementSystem.dto.ResponseDTO.UserResponse;
import com.tms.transportationManagementSystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController
{
    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse createUser(@RequestBody CreateUserRequest request)
    {
        return userService.createUser(request);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAllUsers()
    {
        return userService.getAllUsers();
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public UserResponse getUser(@RequestParam(required = false) Long id)
    {
        return userService.getUser(id);
    }

    @PutMapping
    @PreAuthorize("isAuthenticated()")
    public UserResponse updateUser(@RequestParam(required = false) Long id, @RequestBody CreateUserRequest request)
    {
        return userService.updateUser(id, request);
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteUser(@RequestParam Long id)
    {
        userService.deleteUser(id);
        return "User Deleted Successfully";
    }
}