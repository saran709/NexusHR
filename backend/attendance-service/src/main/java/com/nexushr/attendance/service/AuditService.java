package com.nexushr.attendance.service;

import org.springframework.stereotype.Service;

@Service
public class AuditService {
    public void log(String userEmail, String action, String entityType, String details, String ipAddress) {
        // Audit logging implementation
    }
}
