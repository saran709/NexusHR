package com.nexushr.performance.security;

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
        String role = email.contains("admin") || email.contains("hr") ? "ROLE_HR_ADMIN" : 
                      email.contains("manager") ? "ROLE_MANAGER" : "ROLE_EMPLOYEE";
        return new CustomUserDetails(email, Collections.singletonList(new SimpleGrantedAuthority(role)));
    }
}
