package com.nexushr.payroll.security;

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
        // For payroll service, support payroll admin / HR roles or default EMPLOYEE
        String role = email.contains("admin") || email.contains("payroll") ? "ROLE_HR_ADMIN" : "ROLE_EMPLOYEE";
        return new CustomUserDetails(email, Collections.singletonList(new SimpleGrantedAuthority(role)));
    }
}
