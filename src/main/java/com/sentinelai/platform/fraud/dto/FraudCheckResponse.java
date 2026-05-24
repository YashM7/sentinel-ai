package com.sentinelai.platform.fraud.dto;

import com.sentinelai.platform.fraud.rules.FraudRuleResult;

import java.util.List;

public class FraudCheckResponse {

    private final boolean fraudulent;
    private final List<FraudRuleResult> triggeredRules;

    public FraudCheckResponse(
            boolean fraudulent,
            List<FraudRuleResult> triggeredRules) {
        this.fraudulent = fraudulent;
        this.triggeredRules = triggeredRules;
    }

    public boolean isFraudulent() {
        return fraudulent;
    }

    public List<FraudRuleResult> getTriggeredRules() {
        return triggeredRules;
    }
}