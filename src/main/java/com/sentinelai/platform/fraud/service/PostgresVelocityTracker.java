package com.sentinelai.platform.fraud.service;

import com.sentinelai.platform.transaction.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PostgresVelocityTracker implements VelocityTracker{

    private final TransactionRepository transactionRepository;

    public PostgresVelocityTracker(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public long getTransactionCount(Long userId, LocalDateTime since) {
        return transactionRepository.countByUserIdAndCreatedAtAfter(userId, since);
    }
}