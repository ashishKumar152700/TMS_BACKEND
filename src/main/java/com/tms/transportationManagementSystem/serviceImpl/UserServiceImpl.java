package com.tms.transportationManagementSystem.serviceImpl;

import com.tms.transportationManagementSystem.entity.Role;
import com.tms.transportationManagementSystem.entity.User;
import com.tms.transportationManagementSystem.repository.RoleRepository;
import com.tms.transportationManagementSystem.repository.UserRepository;
import com.tms.transportationManagementSystem.dto.RequestDTO.CreateUserRequest;
import com.tms.transportationManagementSystem.dto.ResponseDTO.UserResponse;
import com.tms.transportationManagementSystem.security.CustomUserDetails;
import com.tms.transportationManagementSystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
                .map(roleId -> roleRepository.findById(roleId).orElseThrow(() -> new RuntimeException("Role not found")))
                .collect(Collectors.toSet());

        User user = new User();

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
    public UserResponse getUser(Long id) {

        User loggedInUser = getUserByToken();

        boolean isAdmin = loggedInUser.getRoles()
                .stream()
                .anyMatch(role ->
                        role.getRoleName().equalsIgnoreCase("ADMIN"));

        if (isAdmin) {

            if (id == null) {
                return mapToResponse(loggedInUser);
            }

            User user = userRepository.findById(id)
                    .orElseThrow(() ->
                            new UsernameNotFoundException("User not found"));

            return mapToResponse(user);
        }

        if (id != null &&
                !loggedInUser.getId().equals(id)) {

            throw new AccessDeniedException(
                    "You are not authorized to view another user");
        }

        return mapToResponse(loggedInUser);
    }

//    @Override
//    public UserResponse updateUser(Long id, CreateUserRequest request) {
//
//        User loggedInUser = getUserByToken();
//
//        boolean isAdmin = loggedInUser.getRoles()
//                .stream()
//                .anyMatch(role ->
//                        role.getRoleName().equalsIgnoreCase("ADMIN"));
//
//        if (!isAdmin) {
//
//            if (id != null &&
//                    !loggedInUser.getId().equals(id)) {
//
//                throw new AccessDeniedException(
//                        "You are not authorized to update another user");
//            }
//
//            id = loggedInUser.getId();
//        }
//
//        User user = userRepository.findById(id)
//                .orElseThrow(() ->
//                        new UsernameNotFoundException("User not found"));
//
//        user.setName(request.getName());
//        user.setEmail(request.getEmail());
//
//        if (request.getPassword() != null &&
//                !request.getPassword().isBlank()) {
//
//            user.setPassword(
//                    passwordEncoder.encode(request.getPassword())
//            );
//        }
//
//        if (isAdmin &&
//                request.getRoleIds() != null &&
//                !request.getRoleIds().isEmpty()) {
//
//            Set<Role> roles = request.getRoleIds()
//                    .stream()
//                    .map(roleId -> roleRepository.findById(roleId)
//                            .orElseThrow(() ->
//                                    new RuntimeException("Role not found")))
//                    .collect(Collectors.toSet());
//
//            user.setRoles(roles);
//
//            boolean isTransporter = roles.stream()
//                    .anyMatch(role ->
//                            role.getRoleName()
//                                    .equalsIgnoreCase("TRANSPORTER"));
//
//            if (isTransporter) {
//
//                if (request.getJdeAddressNumber() == null) {
//                    throw new RuntimeException(
//                            "JDE Address Number required for transporter");
//                }
//
//                user.setJdeAddressNumber(
//                        request.getJdeAddressNumber());
//
//                user.setTransporterName(
//                        request.getTransporterName());
//
//            } else {
//
//                user.setJdeAddressNumber(null);
//                user.setTransporterName(null);
//            }
//        }
//
//        User updatedUser = userRepository.save(user);
//
//        return mapToResponse(updatedUser);
//    }

    @Override
    public UserResponse updateUser(Long id, CreateUserRequest request)
    {

        User loggedInUser = getUserByToken();

        boolean isAdmin = isAdmin(loggedInUser);

        id = validateAndResolveUserId(id, loggedInUser, isAdmin);

        User user = getUserById(id);

        updateBasicDetails(user, request);

        if (isAdmin)
        {
            updateRolesAndTransporterDetails(user, request);
        }

        User updatedUser = userRepository.save(user);

        return mapToResponse(updatedUser);
    }

    private boolean isAdmin(User user)
    {
        return user.getRoles().stream().anyMatch(role -> role.getRoleName().equalsIgnoreCase("ADMIN"));
    }

    private Long validateAndResolveUserId(Long id, User loggedInUser, boolean isAdmin)
    {
        if (!isAdmin)
        {
            if (id != null && !loggedInUser.getId().equals(id))
            {
                throw new AccessDeniedException("You are not authorized to update another user");
            }

            return loggedInUser.getId();
        }

        return id;
    }

    private User getUserById(Long id)
    {
        return userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    private void updateBasicDetails(User user, CreateUserRequest request)
    {
        user.setName(request.getName());

        user.setEmail(request.getEmail());

        if (request.getPassword() != null && !request.getPassword().isBlank())
        {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
    }

    private void updateRolesAndTransporterDetails(User user, CreateUserRequest request)
    {

        if (request.getRoleIds() == null || request.getRoleIds().isEmpty())
        {
            return;
        }

        Set<Role> roles = request.getRoleIds().stream().map(roleId -> roleRepository.findById(roleId).orElseThrow(() -> new RuntimeException("Role not found"))).collect(Collectors.toSet());

        user.setRoles(roles);

        handleTransporterDetails(user, request, roles);
    }

    private void handleTransporterDetails(User user, CreateUserRequest request, Set<Role> roles)
    {
        boolean isTransporter = roles.stream().anyMatch(role -> role.getRoleName().equalsIgnoreCase("TRANSPORTER"));

        if (isTransporter)
        {
            if (request.getJdeAddressNumber() == null)
            {
                throw new RuntimeException("JDE Address Number required for transporter");
            }

            user.setJdeAddressNumber(request.getJdeAddressNumber());
            user.setTransporterName(request.getTransporterName());

        }
        else
        {
            user.setJdeAddressNumber(null);
            user.setTransporterName(null);
        }
    }

    @Override
    public void deleteUser(Long id) {

        User loggedInUser = getUserByToken();

        boolean isAdmin = loggedInUser.getRoles().stream().anyMatch(role -> role.getRoleName().equalsIgnoreCase("ADMIN"));

        if (!isAdmin)
        {
            throw new AccessDeniedException("Only admin can delete users");
        }

        User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        userRepository.delete(user);
    }

    @Override
    public User getUserByToken()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = customUserDetails.getUserId();
        return userRepository.findById(userId).orElseThrow(()-> new UsernameNotFoundException("User not found with the ID : "+userId));
    }

    private UserResponse mapToResponse(User user) {

        UserResponse response = new UserResponse();

        response.setId(user.getId());

        response.setName(user.getName());

        response.setEmail(user.getEmail());

        response.setRoles(user.getRoles().stream().map(Role::getRoleName).collect(Collectors.toSet()));

        return response;
    }
}