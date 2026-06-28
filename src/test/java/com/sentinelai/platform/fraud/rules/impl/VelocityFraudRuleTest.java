package com.sentinelai.platform.fraud.rules.impl;

import com.sentinelai.platform.fraud.rules.FraudRuleResult;
import com.sentinelai.platform.fraud.service.VelocityTracker;
import com.sentinelai.platform.transaction.entity.TransactionEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class VelocityFraudRuleTest {

    @Mock
    private VelocityTracker velocityTracker;

    @InjectMocks
    private VelocityFraudRule rule;

    @Test
    @DisplayName("Should mark transaction as fraud when transaction count exceeds threshold")
    void shouldMarkAsFraudWhenTransactionCountExceedsLimit() {

        Mockito.when(
                velocityTracker.getTransactionCount(
                        Mockito.eq(1L),
                        Mockito.any(LocalDateTime.class)
                )
        ).thenReturn(4L);

        TransactionEntity transaction = new TransactionEntity();
        transaction.setUserId(1L);

        FraudRuleResult result = rule.evaluate(transaction);

        assertTrue(result.isFraudulent());
        assertEquals(rule.getRuleName(), result.getRuleName());

        Mockito.verify(velocityTracker)
                .getTransactionCount(
                        Mockito.eq(1L),
                        Mockito.any(LocalDateTime.class)
                );
    }

    @Test
    @DisplayName("Should NOT mark as fraud when transaction count is normal")
    void shouldNotMarkAsFraudWhenCountIsNormal() {

        Mockito.when(
                velocityTracker.getTransactionCount(
                        Mockito.eq(1L),
                        Mockito.any(LocalDateTime.class)
                )
        ).thenReturn(3L);

        TransactionEntity transaction = new TransactionEntity();
        transaction.setUserId(1L);

        FraudRuleResult result = rule.evaluate(transaction);

        assertFalse(result.isFraudulent());
        assertEquals(rule.getRuleName(), result.getRuleName());

        Mockito.verify(velocityTracker)
                .getTransactionCount(
                        Mockito.eq(1L),
                        Mockito.any(LocalDateTime.class)
                );
    }

}