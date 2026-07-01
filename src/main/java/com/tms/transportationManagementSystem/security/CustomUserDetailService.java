package com.tms.transportationManagementSystem.security;

import com.tms.transportationManagementSystem.entity.User;
import com.tms.transportationManagementSystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService
{
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user =  userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User not found with the provided email "+email));
        return new CustomUserDetails(user);
    }
}
