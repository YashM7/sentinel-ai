package com.sentinelai.platform.transaction.repository;

import com.sentinelai.platform.transaction.entity.TransactionEntity;
import com.sentinelai.platform.transaction.entity.TransactionStatus;
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
public class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    @DisplayName("Should find transaction by transaction id")
    void shouldFindTransactionByTransactionId() {

        transactionRepository.save(
                createTransaction("TXN-1001", 1L, TransactionStatus.APPROVED)
        );

        Optional<TransactionEntity> result = transactionRepository.findByTransactionId("TXN-1001");

        assertAll(
                () -> assertTrue(result.isPresent()),
                () -> assertEquals("TXN-1001", result.get().getTransactionId()),
                () -> assertEquals(TransactionStatus.APPROVED, result.get().getStatus())
        );
    }

    @Test
    @DisplayName("Should return empty when transaction id does not exist")
    void shouldReturnEmptyWhenTransactionIdDoesNotExist() {

        Optional<TransactionEntity> result = transactionRepository.findByTransactionId("Transaction-Not-Found");

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return true when transaction id exists")
    void shouldReturnTrueWhenTransactionIdExists() {

        transactionRepository.save(
                createTransaction("TXN-1002", 1L, TransactionStatus.APPROVED)
        );

        boolean exists = transactionRepository.existsByTransactionId("TXN-1002");

        assertTrue(exists);
    }

    @Test
    @DisplayName("Should return false when transaction id does not exist")
    void shouldReturnFalseWhenTransactionIdDoesNotExist() {

        boolean exists = transactionRepository.existsByTransactionId("NOT-EXISTS");

        assertFalse(exists);
    }

    @Test
    @DisplayName("Should count transactions by status")
    void shouldCountTransactionsByStatus() {

        transactionRepository.save(
                createTransaction("TXN-1003", 1L, TransactionStatus.APPROVED)
        );

        transactionRepository.save(
                createTransaction("TXN-1004", 1L, TransactionStatus.APPROVED)
        );

        transactionRepository.save(
                createTransaction("TXN-1005", 2L, TransactionStatus.FLAGGED)
        );

        assertAll(
                () -> assertEquals(2, transactionRepository.countByStatus(TransactionStatus.APPROVED)),
                () -> assertEquals(1, transactionRepository.countByStatus(TransactionStatus.FLAGGED))
        );
    }

    private TransactionEntity createTransaction(
            String transactionId,
            Long userId,
            TransactionStatus status) {

        TransactionEntity transaction = new TransactionEntity();
        transaction.setTransactionId(transactionId);
        transaction.setUserId(userId);
        transaction.setMerchantId(100L);
        transaction.setAmount(BigDecimal.valueOf(100.00));
        transaction.setCurrency("USD");
        transaction.setTransactionTimestamp(LocalDateTime.now());
        transaction.setStatus(status);

        return transaction;
    }
}