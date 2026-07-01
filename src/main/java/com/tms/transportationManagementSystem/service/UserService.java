package com.tms.transportationManagementSystem.service;

import com.tms.transportationManagementSystem.dto.RequestDTO.CreateUserRequest;
import com.tms.transportationManagementSystem.dto.ResponseDTO.UserResponse;
import com.tms.transportationManagementSystem.entity.User;

import java.util.List;

public interface UserService {

    UserResponse createUser(CreateUserRequest request);

    List<UserResponse> getAllUsers();

    UserResponse getUser(Long id);

    UserResponse updateUser(Long id,CreateUserRequest request);

    void deleteUser(Long id);

    User getUserByToken();
}