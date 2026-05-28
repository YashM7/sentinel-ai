package com.sentinelai.platform.fraud.rules.impl;

import com.sentinelai.platform.fraud.rules.FraudRule;
import com.sentinelai.platform.fraud.rules.FraudRuleResult;
import com.sentinelai.platform.transaction.entity.TransactionEntity;
import com.sentinelai.platform.transaction.repository.TransactionRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class VelocityFraudRule implements FraudRule {

    private static final long MAX_TRANSACTIONS_PER_MINUTE = 3;
    private final TransactionRepository transactionRepository;

    public VelocityFraudRule(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public FraudRuleResult evaluate(TransactionEntity transaction) {
        
        LocalDateTime oneMinuteAgo =
                LocalDateTime.now().minusMinutes(1);

        long recentTransactionCount =
                transactionRepository.countByUserIdAndCreatedAtAfter(
                        transaction.getUserId(),
                        oneMinuteAgo
                );

        boolean fraudulent = recentTransactionCount > MAX_TRANSACTIONS_PER_MINUTE;

        if(fraudulent) {
            return new FraudRuleResult(
                    true,
                    getRuleName(),
                    "Too many transactions within one minute"
            );
        }

        return new FraudRuleResult(
                false,
                getRuleName(),
                "Transaction velocity is normal"
        );
    }

    @Override
    public String getRuleName() {
        return "VelocityFraudRule";
    }
}