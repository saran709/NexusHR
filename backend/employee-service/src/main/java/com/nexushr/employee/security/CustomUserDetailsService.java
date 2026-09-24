package com.nexushr.employee.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Since employees authenticate via Auth Service, microservice validates JWT claims directly.
        // For security context loading, we assign default employee/admin authority or extract from token.
        return new CustomUserDetails(email, Collections.singletonList(new SimpleGrantedAuthority("ROLE_EMPLOYEE")));
    }
}
