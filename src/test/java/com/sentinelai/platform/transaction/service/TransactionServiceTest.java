package com.sentinelai.platform.transaction.service;

import com.sentinelai.platform.alert.service.FraudAlertService;
import com.sentinelai.platform.audit.service.AuditService;
import com.sentinelai.platform.fraud.dto.FraudCheckResponse;
import com.sentinelai.platform.fraud.rules.FraudRuleResult;
import com.sentinelai.platform.fraud.service.FraudDetectionService;
import com.sentinelai.platform.fraudcase.service.FraudCaseService;
import com.sentinelai.platform.common.observability.api.TransactionMetricsRecorder;
import com.sentinelai.platform.transaction.dto.request.CreateTransactionRequest;
import com.sentinelai.platform.transaction.dto.response.TransactionResponse;
import com.sentinelai.platform.transaction.entity.TransactionEntity;
import com.sentinelai.platform.transaction.entity.TransactionStatus;
import com.sentinelai.platform.transaction.mapper.TransactionMapper;
import com.sentinelai.platform.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private FraudDetectionService fraudDetectionService;

    @Mock
    private FraudAlertService fraudAlertService;

    @Mock
    private AuditService auditService;

    @Mock
    private FraudCaseService fraudCaseService;

    @Mock
    private TransactionMetricsRecorder transactionMetricsRecorder;

    private TransactionService transactionService;

    @BeforeEach
    void setup() {
        transactionService = new TransactionService(
                transactionRepository,
                transactionMapper,
                fraudDetectionService,
                fraudAlertService,
                auditService,
                fraudCaseService,
                transactionMetricsRecorder
        );
    }

    @Test
    @DisplayName("Should throw exception when transaction already exists")
    void shouldThrowExceptionWhenTransactionAlreadyExists() {

        CreateTransactionRequest request = new CreateTransactionRequest();
        request.setTransactionId("TXN-1001");

        when(transactionRepository.existsByTransactionId("TXN-1001")
        ).thenReturn(true);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> transactionService.createTransaction(request)
                );

        assertEquals(
                "Transaction already exists with transactionId: TXN-1001",
                exception.getMessage()
        );

        verify(transactionRepository).existsByTransactionId("TXN-1001");

        verify(transactionMapper, never())
                .toEntity(any(CreateTransactionRequest.class));

        verify(transactionRepository, never())
                .save(any(TransactionEntity.class));

        verify(fraudDetectionService, never())
                .evaluateTransaction(any(TransactionEntity.class));

        verify(fraudAlertService, never())
                .createFraudAlerts(any(TransactionEntity.class), anyList());

        verify(fraudCaseService, never())
                .createFraudCase(any(TransactionEntity.class));

        verify(auditService, never())
                .auditLog(anyString(),anyString(), anyString(), anyString());

        verify(transactionMetricsRecorder, times(1))
                .recordTransactionProcessingTime(any(Duration.class));
    }

    @Test
    @DisplayName("Should approve transaction when fraud check passes")
    void shouldApproveTransactionWhenFraudCheckPasses() {

        CreateTransactionRequest request = new CreateTransactionRequest();
        request.setTransactionId("TXN-1002");

        TransactionEntity transaction = new TransactionEntity();
        transaction.setTransactionId("TXN-1002");

        when(transactionRepository.existsByTransactionId(request.getTransactionId())
        ).thenReturn(false);

        when(transactionMapper.toEntity(request)
        ).thenReturn(transaction);

        when(transactionRepository.save(Mockito.any(TransactionEntity.class))
        ).thenAnswer(invocation -> invocation.getArgument(0));

        FraudCheckResponse fraudCheckResponse =
                new FraudCheckResponse(false, List.of());

        when(fraudDetectionService.evaluateTransaction(transaction)
        ).thenReturn(fraudCheckResponse);

        TransactionResponse approvedResponse = new TransactionResponse();
        approvedResponse.setStatus(TransactionStatus.APPROVED);

        when(transactionMapper.toResponse(Mockito.any(TransactionEntity.class))
        ).thenReturn(approvedResponse);

        TransactionResponse response = transactionService.createTransaction(request);

        assertAll(
                () -> assertEquals(TransactionStatus.APPROVED, response.getStatus())
        );

        verify(transactionRepository).existsByTransactionId("TXN-1002");

        verify(transactionMapper).toEntity(request);

        verify(transactionRepository, times(2)
        ).save(any(TransactionEntity.class));

        verify(fraudDetectionService).evaluateTransaction(transaction);

        verify(fraudAlertService, never()
        ).createFraudAlerts(any(), any());

        verify(fraudCaseService, never()
        ).createFraudCase(any());

        verify(auditService).auditLog(
                "TRANSACTION",
                "TXN-1002",
                "TRANSACTION_APPROVED",
                "Transaction approved successfully"
        );

        verify(transactionMetricsRecorder, times(1)).recordTransactionProcessed();

        verify(transactionMetricsRecorder, times(1)).recordTransactionApproved();

        verify(transactionMetricsRecorder, times(1))
                .recordTransactionProcessingTime(any(Duration.class));

        verify(transactionMapper).toResponse(any(TransactionEntity.class));

    }

    @Test
    @DisplayName("Should flag transaction when fraud is detected")
    void shouldFlagTransactionWhenFraudIsDetected() {

        CreateTransactionRequest request = new CreateTransactionRequest();
        request.setTransactionId("TXN-1003");

        TransactionEntity transaction = new TransactionEntity();
        transaction.setTransactionId("TXN-1003");

        when(transactionRepository.existsByTransactionId(request.getTransactionId())
        ).thenReturn(false);

        when(transactionMapper.toEntity(request)
        ).thenReturn(transaction);

        when(transactionRepository.save(any(TransactionEntity.class))
        ).thenAnswer(invocation -> invocation.getArgument(0));

        FraudRuleResult fraudRuleResult =
                new FraudRuleResult(
                        true,
                        "LargeAmountFraudRule",
                        "Transaction amount exceeds fraud threshold"
                );

        FraudCheckResponse fraudCheckResponse =
                new FraudCheckResponse(true, List.of(fraudRuleResult));

        when(fraudDetectionService.evaluateTransaction(transaction)
        ).thenReturn(fraudCheckResponse);

        TransactionResponse flaggedResponse = new TransactionResponse();
        flaggedResponse.setStatus(TransactionStatus.FLAGGED);

        when(transactionMapper.toResponse(any(TransactionEntity.class))
        ).thenReturn(flaggedResponse);

        TransactionResponse response = transactionService.createTransaction(request);

        assertAll(
                () -> assertEquals(TransactionStatus.FLAGGED, response.getStatus())
        );

        verify(transactionRepository).existsByTransactionId("TXN-1003");

        verify(transactionMapper).toEntity(request);

        verify(transactionRepository, times(2)
        ).save(any(TransactionEntity.class));

        verify(fraudDetectionService).evaluateTransaction(transaction);

        verify(fraudAlertService).createFraudAlerts(transaction, fraudCheckResponse.getTriggeredRules());

        verify(fraudCaseService).createFraudCase(transaction);

        verify(auditService).auditLog(
                "TRANSACTION",
                "TXN-1003",
                "TRANSACTION_FLAGGED",
                "Transaction flagged by fraud engine"
        );

        verify(transactionMetricsRecorder, times(1)).recordTransactionProcessed();

        verify(transactionMetricsRecorder, times(1)).recordTransactionFlagged();

        verify(transactionMetricsRecorder, times(1))
                .recordTransactionProcessingTime(any(Duration.class));

        verify(transactionMapper).toResponse(any(TransactionEntity.class));

        verifyNoMoreInteractions(
                transactionRepository,
                transactionMapper,
                fraudDetectionService,
                fraudAlertService,
                fraudCaseService,
                auditService,
                transactionMetricsRecorder
        );
    }
}