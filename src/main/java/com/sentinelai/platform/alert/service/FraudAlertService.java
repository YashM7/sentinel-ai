package com.sentinelai.platform.alert.service;

import com.sentinelai.platform.alert.entity.FraudAlertEntity;
import com.sentinelai.platform.alert.repository.FraudAlertRepository;
import com.sentinelai.platform.fraud.rules.FraudRuleResult;
import com.sentinelai.platform.observability.api.FraudMetricsRecorder;
import com.sentinelai.platform.transaction.entity.TransactionEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FraudAlertService {

    private final FraudAlertRepository fraudAlertRepository;
    private final FraudMetricsRecorder fraudMetricsRecorder;

    public FraudAlertService(FraudAlertRepository fraudAlertRepository, FraudMetricsRecorder fraudMetricsRecorder) {
        this.fraudAlertRepository = fraudAlertRepository;
        this.fraudMetricsRecorder = fraudMetricsRecorder;
    }

    public void createFraudAlerts (
            TransactionEntity transaction,
            List<FraudRuleResult> triggeredRules) {

        for(FraudRuleResult ruleResult : triggeredRules) {

            FraudAlertEntity fraudAlert =
                    new FraudAlertEntity();

            fraudAlert.setTransaction(transaction);
            fraudAlert.setRuleName(ruleResult.getRuleName());
            fraudAlert.setReason(ruleResult.getReason());
            fraudAlert.setCreatedAt(LocalDateTime.now());

            fraudAlertRepository.save(fraudAlert);
            fraudMetricsRecorder.recordFraudAlertCreated();
        }

    }

}