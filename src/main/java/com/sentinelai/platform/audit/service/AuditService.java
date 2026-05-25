package com.sentinelai.platform.audit.service;

import com.sentinelai.platform.audit.entity.AuditLogEntity;
import com.sentinelai.platform.audit.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void auditLog(
            String entityType,
            String entityId,
            String action,
            String details) {

        AuditLogEntity auditLog =
                new AuditLogEntity();

        auditLog.setEntityType(entityType);
        auditLog.setEntityId(entityId);
        auditLog.setAction(action);
        auditLog.setDetails(details);
        auditLog.setCreatedAt(LocalDateTime.now());

        auditLogRepository.save(auditLog);
    }
}