package com.tms.transportationManagementSystem.serviceImpl;

import com.tms.transportationManagementSystem.entity.Role;
import com.tms.transportationManagementSystem.entity.User;
import com.tms.transportationManagementSystem.repository.RoleRepository;
import com.tms.transportationManagementSystem.repository.UserRepository;
import com.tms.transportationManagementSystem.dto.RequestDTO.CreateUserRequest;
import com.tms.transportationManagementSystem.dto.ResponseDTO.UserResponse;
import com.tms.transportationManagementSystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse createUser(CreateUserRequest request) {

        Set<Role> roles = request.getRoleIds()
                .stream()
                .map(roleId -> roleRepository.findById(roleId)
                        .orElseThrow(() -> new RuntimeException("Role not found")))
                .collect(Collectors.toSet());

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        user.setRoles(roles);

        // 🔥 NEW LOGIC (TRANSPORTER ONLY)
        boolean isTransporter = roles.stream()
                .anyMatch(r -> r.getRoleName().equalsIgnoreCase("TRANSPORTER"));

        if (isTransporter) {

            if (request.getJdeAddressNumber() == null) {
                throw new RuntimeException("JDE Address Number required for transporter");
            }

            user.setJdeAddressNumber(request.getJdeAddressNumber());
            user.setTransporterName(request.getTransporterName());
        }

        User savedUser = userRepository.save(user);

        return mapToResponse(savedUser);
    }

    @Override
    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public UserResponse getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return mapToResponse(user);
    }

    @Override
    public UserResponse updateUser(Long id, CreateUserRequest request) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Set<Role> roles = request.getRoleIds()
                .stream()
                .map(roleId -> roleRepository.findById(roleId)
                        .orElseThrow(() -> new RuntimeException("Role not found")))
                .collect(Collectors.toSet());

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        user.setRoles(roles);

        boolean isTransporter = roles.stream()
                .anyMatch(r -> r.getRoleName().equalsIgnoreCase("TRANSPORTER"));

        if (isTransporter) {

            if (request.getJdeAddressNumber() == null) {
                throw new RuntimeException("JDE Address Number required for transporter");
            }

            user.setJdeAddressNumber(request.getJdeAddressNumber());
            user.setTransporterName(request.getTransporterName());

        } else {

            user.setJdeAddressNumber(null);
            user.setTransporterName(null);
        }

        User updatedUser = userRepository.save(user);

        return mapToResponse(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {

        userRepository.deleteById(id);
    }

    private UserResponse mapToResponse(User user) {

        UserResponse response = new UserResponse();

        response.setId(user.getId());

        response.setName(user.getName());

        response.setEmail(user.getEmail());

        response.setRoles(
                user.getRoles()
                        .stream()
                        .map(Role::getRoleName)
                        .collect(Collectors.toSet())
        );

        return response;
    }
}