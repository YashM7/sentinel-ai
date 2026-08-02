package com.sentinelai.platform.common.observability.api;

public interface FraudMetricsRecorder {

    void recordRuleTriggered(String ruleName);

    void recordFraudAlertCreated();

    void recordFraudCaseCreated();
}