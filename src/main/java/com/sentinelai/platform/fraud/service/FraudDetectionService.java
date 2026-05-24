package com.sentinelai.platform.fraud.service;

import com.sentinelai.platform.fraud.dto.FraudCheckResponse;
import com.sentinelai.platform.fraud.rules.FraudRule;
import com.sentinelai.platform.fraud.rules.FraudRuleResult;
import com.sentinelai.platform.transaction.entity.TransactionEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FraudDetectionService {

    private final List<FraudRule> fraudRules;

    public FraudDetectionService(List<FraudRule> fraudRules) {
        this.fraudRules = fraudRules;
    }

    public FraudCheckResponse evaluateTransaction(TransactionEntity transaction) {

        List<FraudRuleResult> triggeredRules = new ArrayList<>();

        for(FraudRule fraudRule : fraudRules) {
            FraudRuleResult result =
                    fraudRule.evaluate(transaction);

            if(result.isFraudulent()) {
                triggeredRules.add(result);
            }
        }

        return new FraudCheckResponse(
                !triggeredRules.isEmpty(),
                triggeredRules
        );
    }
}