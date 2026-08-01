package com.sentinelai.platform.transaction.service;

import com.sentinelai.platform.alert.service.FraudAlertService;
import com.sentinelai.platform.audit.service.AuditService;
import com.sentinelai.platform.fraud.dto.FraudCheckResponse;
import com.sentinelai.platform.fraud.service.FraudDetectionService;
import com.sentinelai.platform.fraudcase.service.FraudCaseService;
import com.sentinelai.platform.observability.api.TransactionMetricsRecorder;
import com.sentinelai.platform.transaction.dto.request.CreateTransactionRequest;
import com.sentinelai.platform.transaction.dto.response.TransactionResponse;
import com.sentinelai.platform.transaction.entity.TransactionEntity;
import com.sentinelai.platform.transaction.entity.TransactionStatus;
import com.sentinelai.platform.transaction.mapper.TransactionMapper;
import com.sentinelai.platform.transaction.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final FraudDetectionService fraudDetectionService;
    private final FraudAlertService fraudAlertService;
    private final AuditService auditService;
    private final FraudCaseService fraudCaseService;
    private final TransactionMetricsRecorder transactionMetricsRecorder;
    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);

    public TransactionService(
            TransactionRepository transactionRepository,
            TransactionMapper transactionMapper,
            FraudDetectionService fraudDetectionService,
            FraudAlertService fraudAlertService,
            AuditService auditService,
            FraudCaseService fraudCaseService,
            TransactionMetricsRecorder transactionMetricsRecorder)
    {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
        this.fraudDetectionService = fraudDetectionService;
        this.fraudAlertService = fraudAlertService;
        this.auditService = auditService;
        this.fraudCaseService = fraudCaseService;
        this.transactionMetricsRecorder = transactionMetricsRecorder;
    }

    @Transactional
    public TransactionResponse createTransaction(CreateTransactionRequest request) {

        if(transactionRepository.existsByTransactionId(request.getTransactionId())) {
            throw new IllegalArgumentException(
                    "Transaction already exists with transactionId: " +
                            request.getTransactionId()
            );
        }

        log.info(
                "Processing transaction with transactionId={}",
                request.getTransactionId()
        );

        TransactionEntity entity = transactionMapper.toEntity(request);
        TransactionEntity savedEntity = transactionRepository.save(entity);

        transactionMetricsRecorder.recordTransactionProcessed();

        FraudCheckResponse fraudCheckResponse =
                fraudDetectionService.evaluateTransaction(savedEntity);

        if(fraudCheckResponse.isFraudulent()) {
            savedEntity.setStatus(TransactionStatus.FLAGGED);
            fraudAlertService.createFraudAlerts(savedEntity, fraudCheckResponse.getTriggeredRules());

            log.warn(
                    "Transaction flagged for fraud. transactionId={}",
                    savedEntity.getTransactionId()
            );

            fraudCaseService.createFraudCase(savedEntity);

            auditService.auditLog(
                    "TRANSACTION",
                    savedEntity.getTransactionId(),
                    "TRANSACTION_FLAGGED",
                    "Transaction flagged by fraud engine"
            );
        }
        else {
            savedEntity.setStatus(TransactionStatus.APPROVED);

            log.info(
                    "Transaction approved. transactionId={}",
                    savedEntity.getTransactionId()
            );

            auditService.auditLog(
                    "TRANSACTION",
                    savedEntity.getTransactionId(),
                    "TRANSACTION_APPROVED",
                    "Transaction approved successfully"
            );
        }

        TransactionEntity updatedTransaction =
                transactionRepository.save(savedEntity);

        if(updatedTransaction.getStatus() == TransactionStatus.APPROVED) {
            transactionMetricsRecorder.recordTransactionApproved();
        } else if (updatedTransaction.getStatus() == TransactionStatus.FLAGGED) {
            transactionMetricsRecorder.recordTransactionFlagged();
        }

        return transactionMapper.toResponse(updatedTransaction);
    }
}