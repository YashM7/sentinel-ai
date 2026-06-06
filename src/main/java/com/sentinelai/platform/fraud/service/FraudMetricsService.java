package com.sentinelai.platform.fraud.service;

import com.sentinelai.platform.alert.repository.FraudAlertRepository;
import com.sentinelai.platform.alert.repository.projection.RuleTriggerCount;
import com.sentinelai.platform.fraud.dto.FraudMetricsResponse;
import com.sentinelai.platform.transaction.entity.TransactionStatus;
import com.sentinelai.platform.transaction.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FraudMetricsService {

    private final TransactionRepository transactionRepository;
    private final FraudAlertRepository fraudAlertRepository;

    public FraudMetricsService(
            TransactionRepository transactionRepository,
            FraudAlertRepository fraudAlertRepository
    ) {
        this.transactionRepository = transactionRepository;
        this.fraudAlertRepository = fraudAlertRepository;
    }

    public FraudMetricsResponse getFraudMetrics() {

        long totalTransactions = transactionRepository.count();
        long approvedTransactions = transactionRepository.countByStatus(TransactionStatus.APPROVED);
        long flaggedTransactions = transactionRepository.countByStatus(TransactionStatus.FLAGGED);
        long totalFraudAlerts = fraudAlertRepository.count();
        List<RuleTriggerCount> results = fraudAlertRepository.countAlertsByRule();

        Map<String, Long> ruleTriggerCounts =
                results.stream()
                        .collect(Collectors.toMap(
                                RuleTriggerCount::getRuleName,
                                RuleTriggerCount::getCount
                        ));

        FraudMetricsResponse response = new FraudMetricsResponse();

        response.setTotalTransactions(totalTransactions);
        response.setApprovedTransactions(approvedTransactions);
        response.setFlaggedTransactions(flaggedTransactions);
        response.setTotalFraudAlerts(totalFraudAlerts);
        response.setRuleTriggerCounts(ruleTriggerCounts);

        return response;
    }
}