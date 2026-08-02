package com.sentinelai.platform.fraud.service;

import com.sentinelai.platform.fraud.dto.FraudCheckResponse;
import com.sentinelai.platform.fraud.rules.FraudRule;
import com.sentinelai.platform.fraud.rules.FraudRuleResult;
import com.sentinelai.platform.common.observability.api.FraudMetricsRecorder;
import com.sentinelai.platform.transaction.entity.TransactionEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FraudDetectionService {

    private final List<FraudRule> fraudRules;
    private final FraudMetricsRecorder fraudMetricsRecorder;

    public FraudDetectionService(List<FraudRule> fraudRules, FraudMetricsRecorder fraudMetricsRecorder) {
        this.fraudRules = fraudRules;
        this.fraudMetricsRecorder = fraudMetricsRecorder;
    }

    public FraudCheckResponse evaluateTransaction(TransactionEntity transaction) {

        List<FraudRuleResult> triggeredRules = new ArrayList<>();

        for(FraudRule fraudRule : fraudRules) {
            FraudRuleResult result =
                    fraudRule.evaluate(transaction);

            if(result.isFraudulent()) {
                triggeredRules.add(result);
                fraudMetricsRecorder.recordRuleTriggered(result.getRuleName());
            }
        }

        return new FraudCheckResponse(
                !triggeredRules.isEmpty(),
                triggeredRules
        );
    }
}