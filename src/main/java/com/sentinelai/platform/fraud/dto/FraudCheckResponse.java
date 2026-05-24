package com.sentinelai.platform.fraud.dto;

import java.util.List;

public class FraudCheckResponse {

    private final boolean fraudulent;
    private final List<String> triggeredRules;

    public FraudCheckResponse(
            boolean fraudulent,
            List<String> triggeredRules) {
        this.fraudulent = fraudulent;
        this.triggeredRules = triggeredRules;
    }

    public boolean isFraudulent() {
        return fraudulent;
    }

    public List<String> getTriggeredRules() {
        return triggeredRules;
    }
}