package com.sentinelai.platform.observability.micrometer;

import com.sentinelai.platform.observability.api.FraudMetricsRecorder;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MicrometerFraudMetricsRecorder implements FraudMetricsRecorder {

    private final MeterRegistry meterRegistry;
    private final Map<String, Counter> ruleCounters = new ConcurrentHashMap<>();
    private final Counter fraudAlertCounter;
    private final Counter fraudCaseCounter;

    private static final String FRAUD_ALERTS_CREATED = "fraud_alerts_created_total";
    private static final String FRAUD_CASES_CREATED = "fraud_cases_created_total";
    private static final String FRAUD_RULE_TRIGGERED = "fraud_rule_triggered_total";

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
}