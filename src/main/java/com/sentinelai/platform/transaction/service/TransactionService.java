package com.sentinelai.platform.transaction.service;

import com.sentinelai.platform.fraud.dto.FraudCheckResponse;
import com.sentinelai.platform.fraud.service.FraudDetectionService;
import com.sentinelai.platform.transaction.dto.request.CreateTransactionRequest;
import com.sentinelai.platform.transaction.dto.response.TransactionResponse;
import com.sentinelai.platform.transaction.entity.TransactionEntity;
import com.sentinelai.platform.transaction.entity.TransactionStatus;
import com.sentinelai.platform.transaction.mapper.TransactionMapper;
import com.sentinelai.platform.transaction.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final FraudDetectionService fraudDetectionService;

    public TransactionService(
            TransactionRepository transactionRepository,
            TransactionMapper transactionMapper,
            FraudDetectionService fraudDetectionService)
    {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
        this.fraudDetectionService = fraudDetectionService;
    }

    @Transactional
    public TransactionResponse createTransaction(CreateTransactionRequest request) {

        if(transactionRepository.existsByTransactionId(request.getTransactionId())) {
            throw new IllegalArgumentException(
                    "Transaction already exists with transactionId: " +
                            request.getTransactionId()
            );
        }

        TransactionEntity entity = transactionMapper.toEntity(request);
        TransactionEntity savedEntity = transactionRepository.save(entity);

        FraudCheckResponse fraudCheckResponse =
                fraudDetectionService.evaluateTransaction(savedEntity);

        if(fraudCheckResponse.isFraudulent()) {
            savedEntity.setStatus(TransactionStatus.FLAGGED);
        }
        else {
            savedEntity.setStatus(TransactionStatus.APPROVED);
        }

        TransactionEntity updatedTransaction =
                transactionRepository.save(savedEntity);

        return transactionMapper.toResponse(updatedTransaction);
    }
}