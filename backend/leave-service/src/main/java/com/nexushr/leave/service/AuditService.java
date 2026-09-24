package com.nexushr.leave.service;

import com.nexushr.leave.entity.AuditLog;
import com.nexushr.leave.repository.AuditLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public void log(String userEmail, String action, String entityType, UUID entityId, String details, String ipAddress) {
        AuditLog log = new AuditLog(userEmail, action, entityType, entityId, details, ipAddress);
        auditLogRepository.save(log);
    }
}
