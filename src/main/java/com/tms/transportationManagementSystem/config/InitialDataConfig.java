
package com.tms.transportationManagementSystem.config;

import com.tms.transportationManagementSystem.entity.Role;
import com.tms.transportationManagementSystem.entity.User;
import com.tms.transportationManagementSystem.repository.RoleRepository;
import com.tms.transportationManagementSystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class InitialDataConfig {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initializeUsersAndRoles() {
        return args -> {

            // 1. Create roles if they do not exist
            Role adminRole = getOrCreateRole("ADMIN");
            Role transporterRole = getOrCreateRole("TRANSPORTER");

            // 2. Create Admin user if it does not exist
            createUserIfNotExists(
                    "TMS Admin",
                    "admin@tms.com",
                    "Admin@123",
                    adminRole,
                    null,
                    null
            );

            // 3. Create Transporter user if it does not exist
            createUserIfNotExists(
                    "Demo Transporter",
                    "transporter@tms.com",
                    "Transporter@123",
                    transporterRole,
                    10001L,
                    "Demo Transporter"
            );
        };
    }

    private Role getOrCreateRole(String roleName) {

        Optional<Role> existingRole = roleRepository.findAll()
                .stream()
                .filter(role -> role.getRoleName() != null
                        && role.getRoleName().trim()
                        .equalsIgnoreCase(roleName))
                .findFirst();

        if (existingRole.isPresent()) {
            return existingRole.get();
        }

        Role role = new Role();
        role.setRoleName(roleName);

        return roleRepository.save(role);
    }

    private void createUserIfNotExists(
            String name,
            String email,
            String rawPassword,
            Role role,
            Long jdeAddressNumber,
            String transporterName
    ) {

        Optional<User> existingUser =
                userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            // Ensure the expected role is assigned without
            // removing any roles the user already has.
            User user = existingUser.get();

            Set<Role> roles = user.getRoles() == null
                    ? new HashSet<>()
                    : new HashSet<>(user.getRoles());

            boolean roleExists = roles.stream().anyMatch(
                    existingRole ->
                            existingRole.getRoleName() != null
                                    && existingRole.getRoleName()
                                    .trim()
                                    .toUpperCase(Locale.ROOT)
                                    .equals(role.getRoleName()
                                            .trim()
                                            .toUpperCase(Locale.ROOT))
            );

            if (!roleExists) {
                roles.add(role);
                user.setRoles(roles);
                userRepository.save(user);
            }

            return;
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));

        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        if (jdeAddressNumber != null) {
            user.setJdeAddressNumber(jdeAddressNumber);
            user.setTransporterName(transporterName);
        }

        userRepository.save(user);
    }
}
