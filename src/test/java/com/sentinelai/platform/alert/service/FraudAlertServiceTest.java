package com.sentinelai.platform.alert.service;

import com.sentinelai.platform.alert.entity.FraudAlertEntity;
import com.sentinelai.platform.alert.repository.FraudAlertRepository;
import com.sentinelai.platform.fraud.rules.FraudRuleResult;
import com.sentinelai.platform.observability.api.FraudMetricsRecorder;
import com.sentinelai.platform.transaction.entity.TransactionEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class FraudAlertServiceTest {

    @Mock
    private FraudAlertRepository fraudAlertRepository;

    @Mock
    private FraudMetricsRecorder fraudMetricsRecorder;

    private FraudAlertService fraudAlertService;

    @BeforeEach
    void setup() {
        fraudAlertService = new FraudAlertService(fraudAlertRepository, fraudMetricsRecorder);
    }

    @Test
    @DisplayName("Should create fraud alerts for triggered rules")
    void shouldCreateFraudAlertsForTriggeredRules() {

        TransactionEntity transaction = new TransactionEntity();

        FraudRuleResult firstFraudRule =
                new FraudRuleResult(
                        true,
                        "LargeAmountFraudRule",
                        "Transaction amount exceeds fraud threshold"
                );

        FraudRuleResult secondFraudRule =
                new FraudRuleResult(
                        true,
                        "VelocityFraudRule",
                        "Too many transactions within one minute"
                );

        fraudAlertService.createFraudAlerts(
                transaction,
                List.of(firstFraudRule, secondFraudRule)
        );

        ArgumentCaptor<FraudAlertEntity> captor =
                ArgumentCaptor.forClass(FraudAlertEntity.class);

        Mockito.verify(fraudAlertRepository, Mockito.times(2))
                .save(captor.capture());

        Mockito.verify(fraudMetricsRecorder, Mockito.times(2)).recordFraudAlertCreated();

        List<FraudAlertEntity> savedAlerts = captor.getAllValues();
        FraudAlertEntity firstAlert = savedAlerts.get(0);
        FraudAlertEntity secondAlert = savedAlerts.get(1);

        assertAll(
                () -> assertSame(transaction, firstAlert.getTransaction()),
                () -> assertEquals("LargeAmountFraudRule", firstAlert.getRuleName()),
                () -> assertEquals("Transaction amount exceeds fraud threshold", firstAlert.getReason()),
                () -> assertNotNull(firstAlert.getCreatedAt()),

                () -> assertSame(transaction, secondAlert.getTransaction()),
                () -> assertEquals("VelocityFraudRule", secondAlert.getRuleName()),
                () -> assertEquals("Too many transactions within one minute", secondAlert.getReason()),
                () -> assertNotNull(secondAlert.getCreatedAt())
        );
    }
}