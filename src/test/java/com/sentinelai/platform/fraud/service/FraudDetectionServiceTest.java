package com.sentinelai.platform.fraud.service;

import com.sentinelai.platform.fraud.dto.FraudCheckResponse;
import com.sentinelai.platform.fraud.rules.FraudRule;
import com.sentinelai.platform.fraud.rules.FraudRuleResult;
import com.sentinelai.platform.transaction.entity.TransactionEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class FraudDetectionServiceTest {

    @Mock
    private FraudRule rule1;

    @Mock
    private FraudRule rule2;

    private FraudDetectionService fraudDetectionService;

    @BeforeEach
    void setup() {
        fraudDetectionService = new FraudDetectionService(List.of(rule1, rule2));
    }

    @Test
    @DisplayName("Should return fraud when one rule detects fraud")
    void shouldReturnFraudWhenOneRuleDetectsFraud() {
        TransactionEntity transaction = new TransactionEntity();

        FraudRuleResult fraudulentResult = new FraudRuleResult(
                true,
                "LargeAmountFraudRule",
                "Transaction amount exceeds fraud threshold"
        );

        FraudRuleResult safeResult = new FraudRuleResult(
                false,
                "VelocityFraudRule",
                "Transaction velocity is normal"
        );

        Mockito.when(
                rule1.evaluate(transaction)
        ).thenReturn(fraudulentResult);

        Mockito.when(
                rule2.evaluate(transaction)
        ).thenReturn(safeResult);

        FraudCheckResponse response = fraudDetectionService.evaluateTransaction(transaction);

        assertAll(
                () -> assertTrue(response.isFraudulent()),
                () -> assertEquals(
                        1,
                        response.getTriggeredRules().size()
                ),
                () -> assertEquals(
                        "LargeAmountFraudRule",
                        response.getTriggeredRules().get(0).getRuleName()
                ),
                () -> assertEquals(
                        "Transaction amount exceeds fraud threshold",
                        response.getTriggeredRules().get(0).getReason()
                )
        );

        Mockito.verify(rule1).evaluate(transaction);
        Mockito.verify(rule2).evaluate(transaction);

    }

    @Test
    @DisplayName("Should return fraud when all rules detect fraud")
    void shouldReturnFraudWhenAllRulesDetectFraud() {
        TransactionEntity transaction = new TransactionEntity();

        FraudRuleResult largeAmountFraudResult = new FraudRuleResult(
                true,
                "LargeAmountFraudRule",
                "Transaction amount exceeds fraud threshold"
        );

        FraudRuleResult velocityFraudResult = new FraudRuleResult(
                true,
                "VelocityFraudRule",
                "Too many transactions within one minute"
        );

        Mockito.when(
                rule1.evaluate(transaction)
        ).thenReturn(largeAmountFraudResult);

        Mockito.when(
                rule2.evaluate(transaction)
        ).thenReturn(velocityFraudResult);

        FraudCheckResponse response = fraudDetectionService.evaluateTransaction(transaction);

        assertAll(
                () -> assertTrue(response.isFraudulent()),
                () -> assertEquals(
                        2,
                        response.getTriggeredRules().size()
                ),
                () -> assertEquals(
                        "LargeAmountFraudRule",
                        response.getTriggeredRules().get(0).getRuleName()
                ),
                () -> assertEquals(
                        "VelocityFraudRule",
                        response.getTriggeredRules().get(1).getRuleName()
                )
        );

        Mockito.verify(rule1).evaluate(transaction);
        Mockito.verify(rule2).evaluate(transaction);

    }

    @Test
    @DisplayName("Should return safe when no rules detect fraud")
    void shouldReturnSafeWhenNoRulesDetectFraud() {
        TransactionEntity transaction = new TransactionEntity();

        FraudRuleResult largeAmountFraudResult = new FraudRuleResult(
                false,
                "LargeAmountFraudRule",
                "Transaction amount is within safe limits"
        );

        FraudRuleResult velocityFraudResult = new FraudRuleResult(
                false,
                "VelocityFraudRule",
                "Transaction velocity is normal"
        );

        Mockito.when(
                rule1.evaluate(transaction)
        ).thenReturn(largeAmountFraudResult);

        Mockito.when(
                rule2.evaluate(transaction)
        ).thenReturn(velocityFraudResult);

        FraudCheckResponse response = fraudDetectionService.evaluateTransaction(transaction);

        assertAll(
                () -> assertFalse(response.isFraudulent()),
                () -> assertEquals(
                        0,
                        response.getTriggeredRules().size()
                )
        );

        Mockito.verify(rule1).evaluate(transaction);
        Mockito.verify(rule2).evaluate(transaction);
    }
}