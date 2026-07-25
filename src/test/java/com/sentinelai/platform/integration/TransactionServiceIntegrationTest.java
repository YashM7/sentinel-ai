package com.sentinelai.platform.integration;

import com.sentinelai.platform.alert.repository.FraudAlertRepository;
import com.sentinelai.platform.audit.repository.AuditLogRepository;
import com.sentinelai.platform.fraudcase.entity.FraudCaseEntity;
import com.sentinelai.platform.fraudcase.entity.FraudCaseStatus;
import com.sentinelai.platform.fraudcase.repository.FraudCaseRepository;
import com.sentinelai.platform.transaction.dto.request.CreateTransactionRequest;
import com.sentinelai.platform.transaction.entity.TransactionEntity;
import com.sentinelai.platform.transaction.entity.TransactionStatus;
import com.sentinelai.platform.transaction.repository.TransactionRepository;
import com.sentinelai.platform.transaction.service.TransactionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class TransactionServiceIntegrationTest {

    private final TransactionService transactionService;
    private final TransactionRepository transactionRepository;
    private final FraudAlertRepository fraudAlertRepository;
    private final AuditLogRepository auditLogRepository;
    private final FraudCaseRepository fraudCaseRepository;

    public TransactionServiceIntegrationTest(
            TransactionService transactionService,
            TransactionRepository transactionRepository,
            FraudAlertRepository fraudAlertRepository,
            AuditLogRepository auditLogRepository,
            FraudCaseRepository fraudCaseRepository)
    {
        this.transactionService = transactionService;
        this.transactionRepository = transactionRepository;
        this.fraudAlertRepository = fraudAlertRepository;
        this.auditLogRepository = auditLogRepository;
        this.fraudCaseRepository = fraudCaseRepository;
    }

    @Test
    @DisplayName("Should create fraud artifacts when transaction triggers fraud rule")
    void createFraudArtifactsWhenTransactionTriggersFraudRule() {

        CreateTransactionRequest request = new CreateTransactionRequest();
        request.setTransactionId("TXN-1001");
        request.setUserId(1L);
        request.setMerchantId(100L);
        request.setAmount(new BigDecimal("15000"));
        request.setCurrency("USD");
        request.setTransactionTimestamp(LocalDateTime.now());

        transactionService.createTransaction(request);

        assertThat(transactionRepository.existsByTransactionId("TXN-1001"))
                .isTrue();

        TransactionEntity transaction =
                transactionRepository.findByTransactionId("TXN-1001").orElseThrow();

        assertThat(transaction.getStatus())
                .isEqualTo(TransactionStatus.FLAGGED);

        assertThat(fraudAlertRepository.count())
                .isEqualTo(1);

        FraudCaseEntity fraudCase = fraudCaseRepository.findAll().get(0);

        assertThat(fraudCase.getCaseNumber())
                .isNotBlank();

        assertThat(fraudCase.getStatus())
                .isEqualTo(FraudCaseStatus.OPEN);

        assertThat(fraudCase.getTransaction().getTransactionId())
                .isEqualTo("TXN-1001");

        assertThat(auditLogRepository.count())
                .isEqualTo(1);
    }

    @Test
    @DisplayName("Should approve transaction without creating fraud artifacts when transaction passes fraud rules")
    void approveTransactionWithoutCreatingFraudArtifacts() {

        CreateTransactionRequest request = new CreateTransactionRequest();
        request.setTransactionId("TXN-1002");
        request.setUserId(1L);
        request.setMerchantId(100L);
        request.setAmount(new BigDecimal("150"));
        request.setCurrency("USD");
        request.setTransactionTimestamp(LocalDateTime.now());

        transactionService.createTransaction(request);

        assertThat(transactionRepository.existsByTransactionId("TXN-1002"))
                .isTrue();

        TransactionEntity transaction =
                transactionRepository.findByTransactionId("TXN-1002").orElseThrow();

        assertThat(transaction.getStatus())
                .isEqualTo(TransactionStatus.APPROVED);

        assertThat(fraudAlertRepository.count())
                .isZero();

        assertThat(fraudCaseRepository.count())
                .isZero();

        assertThat(auditLogRepository.count())
                .isEqualTo(1);
    }

    @Test
    @DisplayName("Should reject duplicate transaction without creating additional artifacts")
    void rejectDuplicateTransactionWithoutCreatingAdditionalArtifacts() {

        CreateTransactionRequest request = new CreateTransactionRequest();
        request.setTransactionId("TXN-1003");
        request.setUserId(1L);
        request.setMerchantId(100L);
        request.setAmount(new BigDecimal("150"));
        request.setCurrency("USD");
        request.setTransactionTimestamp(LocalDateTime.now());

        transactionService.createTransaction(request);

        assertThat(transactionRepository.existsByTransactionId("TXN-1003"))
                .isTrue();

        TransactionEntity transaction =
                transactionRepository.findByTransactionId("TXN-1003").orElseThrow();

        assertThat(transaction.getStatus())
                .isEqualTo(TransactionStatus.APPROVED);

        assertThat(fraudAlertRepository.count())
                .isZero();

        assertThat(fraudCaseRepository.count())
                .isZero();

        assertThat(auditLogRepository.count())
                .isEqualTo(1);

        assertThatThrownBy(() ->
                transactionService.createTransaction(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("TXN-1003");

        assertThat(transactionRepository.count())
                .isEqualTo(1);

        assertThat(fraudAlertRepository.count())
                .isZero();

        assertThat(fraudCaseRepository.count())
                .isZero();

        assertThat(auditLogRepository.count())
                .isEqualTo(1);
    }
}