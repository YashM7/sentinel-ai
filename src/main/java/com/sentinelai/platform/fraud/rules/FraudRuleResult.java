package com.sentinelai.platform.fraud.rules;

public class FraudRuleResult {

    private final boolean fraudulent;
    private final String reason;

    public FraudRuleResult(boolean fraudulent, String reason) {
        this.fraudulent = fraudulent;
        this.reason = reason;
    }

    public boolean isFraudulent() {
        return fraudulent;
    }

    public String getReason() {
        return reason;
    }
}