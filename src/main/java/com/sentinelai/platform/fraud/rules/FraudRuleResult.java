package com.sentinelai.platform.fraud.rules;

public class FraudRuleResult {

    private final boolean fraudulent;
    private final String ruleName;
    private final String reason;

    public FraudRuleResult(boolean fraudulent, String ruleName, String reason) {
        this.fraudulent = fraudulent;
        this.ruleName = ruleName;
        this.reason = reason;
    }

    public boolean isFraudulent() {
        return fraudulent;
    }

    public String getRuleName() {
        return ruleName;
    }

    public String getReason() {
        return reason;
    }
}