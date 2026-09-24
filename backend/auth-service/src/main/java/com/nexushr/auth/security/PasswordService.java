package com.nexushr.auth.security;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {

    private final Argon2PasswordEncoder passwordEncoder;

    public PasswordService() {
        // Argon2id parameters: saltLength=16, hashLength=32, parallelism=1, memory=16384 (16MB), iterations=2
        this.passwordEncoder = new Argon2PasswordEncoder(16, 32, 1, 16384, 2);
    }

    public String encode(CharSequence rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
