package com.sentinelai.platform.fraudcase.repository;

import com.sentinelai.platform.fraudcase.entity.FraudCaseEntity;
import com.sentinelai.platform.fraudcase.entity.FraudCaseStatus;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class FraudCaseRepositoryTest {

    @Autowired
    private FraudCaseRepository fraudCaseRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    @DisplayName("Should find fraud case by case number")
    void shouldFindFraudCaseByCaseNumber() {

        TransactionEntity transaction = new TransactionEntity();
        transaction.setTransactionId("TXN-1001");
        transaction.setUserId(1L);
        transaction.setMerchantId(1L);
        transaction.setAmount(BigDecimal.valueOf(100.00));
        transaction.setCurrency("USD");
        transaction.setTransactionTimestamp(LocalDateTime.now());
        transaction.setStatus(TransactionStatus.FLAGGED);

        transactionRepository.save(transaction);

        FraudCaseEntity fraudCase = new FraudCaseEntity();
        fraudCase.setCaseNumber("CASE-20260721-X5259V34");
        fraudCase.setStatus(FraudCaseStatus.OPEN);
        fraudCase.setCreatedAt(LocalDateTime.now());
        fraudCase.setUpdatedAt(LocalDateTime.now());
        fraudCase.setTransaction(transaction);

        fraudCaseRepository.save(fraudCase);

        Optional<FraudCaseEntity> result = fraudCaseRepository.findByCaseNumber("CASE-20260721-X5259V34");

        assertAll(
                () -> assertTrue(result.isPresent()),
                () -> assertEquals("CASE-20260721-X5259V34", result.get().getCaseNumber()),
                () -> assertEquals(FraudCaseStatus.OPEN, result.get().getStatus())
        );
    }

    @Test
    @DisplayName("Should return empty when fraud case does not exist")
    void shouldReturnEmptyWhenFraudCaseDoesNotExist() {

        Optional<FraudCaseEntity> result = fraudCaseRepository.findByCaseNumber("CASE-NOT-FOUND");

        assertTrue(result.isEmpty());
    }
}