package com.sentinelai.platform.fraud.rules.impl;

import com.sentinelai.platform.fraud.rules.FraudRuleResult;
import com.sentinelai.platform.transaction.entity.TransactionEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class LargeAmountFraudRuleTest {

    private LargeAmountFraudRule rule;

    @BeforeEach
    void setup() {
        rule = new LargeAmountFraudRule();
    }

    @Test
    @DisplayName("Should mark transaction as fraudulent when amount exceeds threshold")
    void shouldMarkTransactionAsFraudulentWhenAmountExceedsThreshold() {
        TransactionEntity transaction = new TransactionEntity();

        transaction.setAmount(BigDecimal.valueOf(15_000));

        FraudRuleResult result = rule.evaluate(transaction);

        assertAll(
                () -> assertTrue(result.isFraudulent()),
                () -> assertEquals(
                        rule.getRuleName(),
                        result.getRuleName()
                ),
                () -> assertEquals(
                        "Transaction amount exceeds fraud threshold",
                        result.getReason()
                )
        );
    }

    @Test
    @DisplayName("Should not mark transaction as fraudulent when amount is below threshold")
    void shouldNotMarkTransactionAsFraudulentWhenAmountIsBelowThreshold() {
        TransactionEntity transaction = new TransactionEntity();

        transaction.setAmount(BigDecimal.valueOf(5_000));

        FraudRuleResult result = rule.evaluate(transaction);

        assertAll(
                () -> assertFalse(result.isFraudulent()),
                () -> assertEquals(
                        rule.getRuleName(),
                        result.getRuleName()
                ),
                () -> assertEquals(
                        "Transaction amount is within safe limits",
                        result.getReason()
                )
        );
    }

    @Test
    @DisplayName("Should not mark transaction as fraudulent when amount equals threshold")
    void shouldNotMarkTransactionAsFraudulentWhenAmountEqualsThreshold() {
        TransactionEntity transaction = new TransactionEntity();

        transaction.setAmount(BigDecimal.valueOf(10_000));

        FraudRuleResult result = rule.evaluate(transaction);

        assertAll(
                () -> assertFalse(result.isFraudulent()),
                () -> assertEquals(
                        rule.getRuleName(),
                        result.getRuleName()
                ),
                () -> assertEquals(
                        "Transaction amount is within safe limits",
                        result.getReason()
                )
        );
    }
}