package com.sentinelai.platform.common.observability.api;

import java.time.Duration;

public interface FraudMetricsRecorder {

    void recordRuleTriggered(String ruleName);

    void recordFraudAlertCreated();

    void recordFraudCaseCreated();

    void recordFraudDetectionDuration(Duration duration);
}