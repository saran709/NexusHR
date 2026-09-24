package com.nexushr.auth.service;

import com.nexushr.auth.dto.*;
import com.nexushr.auth.entity.Role;
import com.nexushr.auth.entity.User;
import com.nexushr.auth.repository.RefreshTokenRepository;
import com.nexushr.auth.repository.RoleRepository;
import com.nexushr.auth.repository.UserRepository;
import com.nexushr.auth.security.CustomUserDetails;
import com.nexushr.auth.security.JwtService;
import com.nexushr.auth.security.PasswordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordService passwordService;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterSuccess() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@nexushr.com");
        request.setPassword("password123");
        request.setFirstName("John");
        request.setLastName("Doe");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordService.encode(request.getPassword())).thenReturn("encodedHash");
        
        Role role = new Role("EMPLOYEE");
        when(roleRepository.findByName("EMPLOYEE")).thenReturn(Optional.of(role));
        
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setEmail(request.getEmail());
        savedUser.setFirstName("John");
        savedUser.setLastName("Doe");
        savedUser.setPasswordHash("encodedHash");
        savedUser.getRoles().add(role);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponse response = authenticationService.register(request);

        assertNotNull(response);
        assertEquals("test@nexushr.com", response.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterDuplicateEmail() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@nexushr.com");
        request.setPassword("password123");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            authenticationService.register(request);
        });

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testLoginSuccess() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@nexushr.com");
        request.setPassword("password123");

        User user = new User();
        user.setId(1L);
        user.setEmail(request.getEmail());
        user.setActive(true);
        Role role = new Role("EMPLOYEE");
        user.getRoles().add(role);

        CustomUserDetails userDetails = new CustomUserDetails(user);
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any())).thenReturn("mockJwtToken");

        AuthResponse response = authenticationService.login(request, "127.0.0.1", "Mozilla");

        assertNotNull(response);
        assertEquals("mockJwtToken", response.getAccessToken());
        assertNotNull(response.getRefreshToken());
    }

    @Test
    void testLoginWrongPassword() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@nexushr.com");
        request.setPassword("wrongpassword");

        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> {
            authenticationService.login(request, "127.0.0.1", "Mozilla");
        });
    }
}
