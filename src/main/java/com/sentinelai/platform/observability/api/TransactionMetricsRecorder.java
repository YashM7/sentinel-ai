package com.sentinelai.platform.observability.api;

public interface TransactionMetricsRecorder {

    void recordTransactionProcessed();

    void recordTransactionApproved();

    void recordTransactionFlagged();
}
