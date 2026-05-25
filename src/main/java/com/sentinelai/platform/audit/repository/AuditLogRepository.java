package com.sentinelai.platform.audit.repository;

import com.sentinelai.platform.audit.entity.AuditLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLogEntity, Long> {
}
