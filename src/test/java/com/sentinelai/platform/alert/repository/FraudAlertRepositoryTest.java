package com.sentinelai.platform.alert.repository;

import com.sentinelai.platform.alert.entity.FraudAlertEntity;
import com.sentinelai.platform.alert.repository.projection.RuleTriggerCount;
import com.sentinelai.platform.testsupport.BasePostgresTest;
import com.sentinelai.platform.transaction.entity.TransactionEntity;
import com.sentinelai.platform.transaction.entity.TransactionStatus;
import com.sentinelai.platform.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
public class FraudAlertRepositoryTest extends BasePostgresTest {
    @Autowired
    private FraudAlertRepository fraudAlertRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    @DisplayName("Should count fraud alerts grouped by rule name")
    void shouldCountFraudAlertsGroupedByRuleName() {

        TransactionEntity transaction = new TransactionEntity();

        transaction.setTransactionId("TXN-1001");
        transaction.setUserId(1L);
        transaction.setMerchantId(100L);
        transaction.setAmount(BigDecimal.valueOf(15_000));
        transaction.setCurrency("USD");
        transaction.setTransactionTimestamp(LocalDateTime.now());
        transaction.setStatus(TransactionStatus.FLAGGED);

        transactionRepository.save(transaction);

        FraudAlertEntity alert1 = new FraudAlertEntity();
        alert1.setRuleName("LargeAmountFraudRule");
        alert1.setReason("Transaction amount exceeds fraud threshold");
        alert1.setCreatedAt(LocalDateTime.now());
        alert1.setTransaction(transaction);

        FraudAlertEntity alert2 = new FraudAlertEntity();
        alert2.setRuleName("LargeAmountFraudRule");
        alert2.setReason("Transaction amount exceeds fraud threshold");
        alert2.setCreatedAt(LocalDateTime.now());
        alert2.setTransaction(transaction);

        FraudAlertEntity alert3 = new FraudAlertEntity();
        alert3.setRuleName("VelocityFraudRule");
        alert3.setReason("Too many transactions within one minute");
        alert3.setCreatedAt(LocalDateTime.now());
        alert3.setTransaction(transaction);

        fraudAlertRepository.save(alert1);
        fraudAlertRepository.save(alert2);
        fraudAlertRepository.save(alert3);

        List<RuleTriggerCount> result = fraudAlertRepository.countAlertsByRule();

        Map<String, Long> counts =
                result.stream()
                        .collect(Collectors.toMap(
                                RuleTriggerCount::getRuleName,
                                RuleTriggerCount::getCount
                        ));

        assertAll(
                () -> assertEquals(2L, counts.get("LargeAmountFraudRule")),
                () -> assertEquals(1L, counts.get("VelocityFraudRule"))
        );
    }
}