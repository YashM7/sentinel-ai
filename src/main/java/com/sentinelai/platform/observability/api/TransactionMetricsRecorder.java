package com.sentinelai.platform.observability.api;

import java.time.Duration;

public interface TransactionMetricsRecorder {

    void recordTransactionProcessed();

    void recordTransactionApproved();

    void recordTransactionFlagged();

    void recordTransactionProcessingTime(Duration duration);
}
