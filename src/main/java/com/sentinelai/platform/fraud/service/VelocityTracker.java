package com.sentinelai.platform.fraud.service;

import java.time.LocalDateTime;

public interface VelocityTracker {
    long getTransactionCount(
            Long userId,
            LocalDateTime since
    );
}
