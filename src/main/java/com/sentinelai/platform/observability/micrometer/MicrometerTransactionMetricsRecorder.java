package com.sentinelai.platform.observability.micrometer;

import com.sentinelai.platform.observability.api.TransactionMetricsRecorder;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class MicrometerTransactionMetricsRecorder implements TransactionMetricsRecorder {

    private final MeterRegistry meterRegistry;
    private final Counter transactionProcessedCounter;
    private final Counter transactionApprovedCounter;
    private final Counter transactionFlaggedCounter;
    private final Timer transactionProcessingTimer;

    private static final String TRANSACTIONS_PROCESSED = "transactions_processed_total";
    private static final String TRANSACTIONS_APPROVED = "transactions_approved_total";
    private static final String TRANSACTIONS_FLAGGED = "transactions_flagged_total";
    private static final String TRANSACTIONS_PROCESSING_DURATION = "transaction_processing_time_total";

    public MicrometerTransactionMetricsRecorder(MeterRegistry meterRegistry) {

        this.meterRegistry = meterRegistry;

        this.transactionProcessedCounter =
                Counter.builder(TRANSACTIONS_PROCESSED)
                        .description("Number of transactions processed")
                        .register(meterRegistry);

        this.transactionApprovedCounter =
                Counter.builder(TRANSACTIONS_APPROVED)
                        .description("Number of approved transactions")
                        .register(meterRegistry);

        this.transactionFlaggedCounter =
                Counter.builder(TRANSACTIONS_FLAGGED)
                        .description("Number of flagged transactions")
                        .register(meterRegistry);

        this.transactionProcessingTimer =
                Timer.builder(TRANSACTIONS_PROCESSING_DURATION)
                        .description("Time taken to process transactions")
                        .register(meterRegistry);
    }

    @Override
    public void recordTransactionProcessed() {
        transactionProcessedCounter.increment();
    }

    @Override
    public void recordTransactionApproved() {
        transactionApprovedCounter.increment();
    }

    @Override
    public void recordTransactionFlagged() {
        transactionFlaggedCounter.increment();
    }

    @Override
    public void recordTransactionProcessingTime(Duration duration) {
        transactionProcessingTimer.record(duration);
    }
}