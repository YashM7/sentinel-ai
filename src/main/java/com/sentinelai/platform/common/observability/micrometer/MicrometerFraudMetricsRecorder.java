package com.sentinelai.platform.common.observability.micrometer;

import com.sentinelai.platform.common.observability.api.FraudMetricsRecorder;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MicrometerFraudMetricsRecorder implements FraudMetricsRecorder {

    private final MeterRegistry meterRegistry;
    private final Map<String, Counter> ruleCounters = new ConcurrentHashMap<>();
    private final Counter fraudAlertCounter;
    private final Counter fraudCaseCounter;
    private final Timer fraudProcessingTimer;

    private static final String FRAUD_ALERTS_CREATED = "fraud_alerts_created_total";
    private static final String FRAUD_CASES_CREATED = "fraud_cases_created_total";
    private static final String FRAUD_RULE_TRIGGERED = "fraud_rule_triggered_total";
    private static final String FRAUD_DETECTION_DURATION = "fraud_detection_duration_total";

    public MicrometerFraudMetricsRecorder(MeterRegistry meterRegistry)
    {
        this.meterRegistry = meterRegistry;

        this.fraudAlertCounter =
                Counter.builder(FRAUD_ALERTS_CREATED)
                        .description("Number of fraud alerts created")
                        .register(meterRegistry);

        this.fraudCaseCounter =
                Counter.builder(FRAUD_CASES_CREATED)
                        .description("Number of fraud cases created")
                        .register(meterRegistry);

        this.fraudProcessingTimer =
                Timer.builder(FRAUD_DETECTION_DURATION)
                        .description("Time taken to evaluate fraud rules")
                        .register(meterRegistry);
    }

    @Override
    public void recordRuleTriggered(String ruleName) {

        Counter counter = ruleCounters.computeIfAbsent(
                ruleName,
                name -> Counter.builder(FRAUD_RULE_TRIGGERED)
                                .tag("rule", name)
                                .description("Number of fraud rule triggers")
                                .register(meterRegistry)
        );
        counter.increment();
    }

    @Override
    public void recordFraudAlertCreated() {
        fraudAlertCounter.increment();
    }

    @Override
    public void recordFraudCaseCreated() {
        fraudCaseCounter.increment();
    }

    @Override
    public void recordFraudDetectionDuration(Duration duration) {
        fraudProcessingTimer.record(duration);
    }
}