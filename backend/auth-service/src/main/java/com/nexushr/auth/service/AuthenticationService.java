package com.nexushr.auth.service;

import com.nexushr.auth.dto.*;
import com.nexushr.auth.entity.RefreshToken;
import com.nexushr.auth.entity.Role;
import com.nexushr.auth.entity.User;
import com.nexushr.auth.repository.RefreshTokenRepository;
import com.nexushr.auth.repository.RoleRepository;
import com.nexushr.auth.repository.UserRepository;
import com.nexushr.auth.security.CustomUserDetails;
import com.nexushr.auth.security.JwtService;
import com.nexushr.auth.security.PasswordService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordService passwordService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private static final long REFRESH_TOKEN_DURATION_MS = 7 * 24 * 60 * 60 * 1000L; // 7 days

    public AuthenticationService(UserRepository userRepository,
                                 RoleRepository roleRepository,
                                 RefreshTokenRepository refreshTokenRepository,
                                 PasswordService passwordService,
                                 JwtService jwtService,
                                 AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordService = passwordService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordService.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setActive(true);
        user.setEmailVerified(false);

        String roleName = request.getRole() != null ? request.getRole() : "EMPLOYEE";
        Role role = roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.findByName("EMPLOYEE")
                        .orElseThrow(() -> new RuntimeException("Default role EMPLOYEE not found")));
        
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        User savedUser = userRepository.save(user);
        return new UserResponse(savedUser);
    }

    @Transactional
    public AuthResponse login(LoginRequest request, String ipAddress, String userAgent) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!user.isActive()) {
            throw new IllegalStateException("Account is deactivated");
        }

        String accessToken = jwtService.generateToken(userDetails);
        String refreshTokenStr = createRefreshToken(user, ipAddress, userAgent);

        return new AuthResponse(accessToken, refreshTokenStr, new UserResponse(user));
    }

    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request, String ipAddress, String userAgent) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new IllegalArgumentException("Refresh token not found"));

        if (refreshToken.isRevoked()) {
            throw new IllegalArgumentException("Refresh token has been revoked");
        }

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new IllegalArgumentException("Refresh token has expired");
        }

        User user = refreshToken.getUser();
        if (!user.isActive()) {
            throw new IllegalStateException("Account is deactivated");
        }

        // Refresh token rotation: revoke old token and issue new one
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        CustomUserDetails userDetails = new CustomUserDetails(user);
        String newAccessToken = jwtService.generateToken(userDetails);
        String newRefreshTokenStr = createRefreshToken(user, ipAddress, userAgent);

        return new AuthResponse(newAccessToken, newRefreshTokenStr, new UserResponse(user));
    }

    @Transactional
    public void logout(String refreshTokenStr) {
        if (refreshTokenStr != null && !refreshTokenStr.isEmpty()) {
            refreshTokenRepository.findByToken(refreshTokenStr).ifPresent(token -> {
                token.setRevoked(true);
                refreshTokenRepository.save(token);
            });
        }
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email"));

        String resetToken = UUID.randomUUID().toString();
        user.setResetToken(resetToken);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(user);
        // In production, trigger notification service email.
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByResetToken(request.getToken())
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired password reset token"));

        if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Password reset token has expired");
        }

        user.setPasswordHash(passwordService.encode(request.getNewPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new UserResponse(user);
    }

    private String createRefreshToken(User user, String ipAddress, String userAgent) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(REFRESH_TOKEN_DURATION_MS));
        refreshToken.setRevoked(false);
        refreshToken.setIpAddress(ipAddress);
        refreshToken.setUserAgent(userAgent);

        refreshTokenRepository.save(refreshToken);
        return refreshToken.getToken();
    }
}
