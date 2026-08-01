package com.sentinelai.platform.observability.api;

public interface FraudMetricsRecorder {

    void recordRuleTriggered(String ruleName);

    void recordFraudAlertCreated();

    void recordFraudCaseCreated();
}