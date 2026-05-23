package com.sentinelai.platform.transaction.service;

import com.sentinelai.platform.transaction.dto.request.CreateTransactionRequest;
import com.sentinelai.platform.transaction.dto.response.TransactionResponse;
import com.sentinelai.platform.transaction.entity.TransactionEntity;
import com.sentinelai.platform.transaction.mapper.TransactionMapper;
import com.sentinelai.platform.transaction.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    public TransactionService(
            TransactionRepository transactionRepository,
            TransactionMapper transactionMapper)
    {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
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

        return transactionMapper.toResponse(savedEntity);
    }
}