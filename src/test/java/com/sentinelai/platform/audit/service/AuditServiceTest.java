package com.sentinelai.platform.audit.service;

import com.sentinelai.platform.audit.entity.AuditLogEntity;
import com.sentinelai.platform.audit.repository.AuditLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class AuditServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    private AuditService auditService;

    @BeforeEach
    void setup() {
        auditService = new AuditService(auditLogRepository);
    }

    @Test
    @DisplayName("Should create audit log and save it()")
    void shouldCreateAuditLogAndSaveIt() {

        String entityType = "TRANSACTION";
        String entityId = "TXN-1001";
        String action = "TRANSACTION_APPROVED";
        String details = "Transaction approved successfully";

        auditService.auditLog(
                entityType,
                entityId,
                action,
                details
        );

        ArgumentCaptor<AuditLogEntity> captor =
                ArgumentCaptor.forClass(AuditLogEntity.class);

        Mockito.verify(auditLogRepository).save(captor.capture());

        AuditLogEntity savedAuditLog = captor.getValue();

        assertAll(
                () -> assertEquals(entityType, savedAuditLog.getEntityType()),
                () -> assertEquals(entityId, savedAuditLog.getEntityId()),
                () -> assertEquals(action, savedAuditLog.getAction()),
                () -> assertEquals(details, savedAuditLog.getDetails()),
                () -> assertNotNull(savedAuditLog.getCreatedAt())
        );
    }
}