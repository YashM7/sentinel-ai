package com.sentinelai.platform.fraud.service;

import com.sentinelai.platform.alert.repository.FraudAlertRepository;
import com.sentinelai.platform.alert.repository.projection.RuleTriggerCount;
import com.sentinelai.platform.fraud.dto.FraudMetricsResponse;
import com.sentinelai.platform.transaction.entity.TransactionStatus;
import com.sentinelai.platform.transaction.repository.TransactionRepository;
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
public class FraudMetricsServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private FraudAlertRepository fraudAlertRepository;

    private FraudMetricsService fraudMetricsService;

    @BeforeEach
    void setup() {
        fraudMetricsService = new FraudMetricsService(transactionRepository, fraudAlertRepository);
    }

    @Test
    @DisplayName("Should return fraud metrics with rule trigger counts")
    void shouldReturnFraudMetricsWithRuleTriggerCounts() {

        Mockito.when(
                transactionRepository.count()
        ).thenReturn(100L);

        Mockito.when(
                transactionRepository.countByStatus(TransactionStatus.APPROVED)
        ).thenReturn(70L);

        Mockito.when(
                transactionRepository.countByStatus(TransactionStatus.FLAGGED)
        ).thenReturn(30L);

        Mockito.when(
                fraudAlertRepository.count()
        ).thenReturn(30L);

        RuleTriggerCount rule1 = Mockito.mock(RuleTriggerCount.class);

        Mockito.when(
                rule1.getRuleName()
        ).thenReturn("LargeAmountFraudRule");

        Mockito.when(
                rule1.getCount()
        ).thenReturn(20L);

        RuleTriggerCount rule2 = Mockito.mock(RuleTriggerCount.class);

        Mockito.when(
                rule2.getRuleName()
        ).thenReturn("VelocityFraudRule");

        Mockito.when(
                rule2.getCount()
        ).thenReturn(10L);

        Mockito.when(
                fraudAlertRepository.countAlertsByRule()
        ).thenReturn(List.of(rule1,rule2));

        FraudMetricsResponse response =
                fraudMetricsService.getFraudMetrics();

        assertAll(
                () -> assertEquals(100L, response.getTotalTransactions()),
                () -> assertEquals(70L, response.getApprovedTransactions()),
                () -> assertEquals(30L, response.getFlaggedTransactions()),
                () -> assertEquals(30L, response.getTotalFraudAlerts()),
                () -> assertEquals(2, response.getRuleTriggerCounts().size()),
                () -> assertEquals(20L, response.getRuleTriggerCounts().get("LargeAmountFraudRule")),
                () -> assertEquals(10L, response.getRuleTriggerCounts().get("VelocityFraudRule"))
        );

        Mockito.verify(transactionRepository).count();
        Mockito.verify(transactionRepository).countByStatus(TransactionStatus.APPROVED);
        Mockito.verify(transactionRepository).countByStatus(TransactionStatus.FLAGGED);
        Mockito.verify(fraudAlertRepository).count();
        Mockito.verify(fraudAlertRepository).countAlertsByRule();
    }

    @Test
    @DisplayName("Should return empty fraud metrics when no transactions or alerts exist")
    void shouldReturnEmptyMetricsWhenNoDataExists() {

        Mockito.when(
                transactionRepository.count()
        ).thenReturn(0L);

        Mockito.when(
                transactionRepository.countByStatus(TransactionStatus.APPROVED)
        ).thenReturn(0L);

        Mockito.when(
                transactionRepository.countByStatus(TransactionStatus.FLAGGED)
        ).thenReturn(0L);

        Mockito.when(
                fraudAlertRepository.count()
        ).thenReturn(0L);

        Mockito.when(
                fraudAlertRepository.countAlertsByRule()
        ).thenReturn(List.of());

        FraudMetricsResponse response =
                fraudMetricsService.getFraudMetrics();

        assertAll(
                () -> assertEquals(0L, response.getTotalTransactions()),
                () -> assertEquals(0L, response.getApprovedTransactions()),
                () -> assertEquals(0L, response.getFlaggedTransactions()),
                () -> assertEquals(0L, response.getTotalFraudAlerts()),
                () -> assertTrue(response.getRuleTriggerCounts().isEmpty())
        );

        Mockito.verify(transactionRepository).count();
        Mockito.verify(transactionRepository).countByStatus(TransactionStatus.APPROVED);
        Mockito.verify(transactionRepository).countByStatus(TransactionStatus.FLAGGED);
        Mockito.verify(fraudAlertRepository).count();
        Mockito.verify(fraudAlertRepository).countAlertsByRule();
    }
}