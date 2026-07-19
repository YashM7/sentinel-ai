package com.sentinelai.platform.transaction.mapper;

import com.sentinelai.platform.transaction.dto.request.CreateTransactionRequest;
import com.sentinelai.platform.transaction.dto.response.TransactionResponse;
import com.sentinelai.platform.transaction.entity.TransactionEntity;
import com.sentinelai.platform.transaction.entity.TransactionStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionMapperTest {

    private final TransactionMapper transactionMapper = new TransactionMapper();

    @Test
    @DisplayName("Should map create transaction request to entity with PENDING status")
    void shouldMapCreateTransactionRequestToEntityWithPendingStatus() {

        CreateTransactionRequest request = new CreateTransactionRequest();

        LocalDateTime transactionTime = LocalDateTime.now();

        request.setTransactionId("TXN-1001");
        request.setUserId(10L);
        request.setMerchantId(20L);
        request.setAmount(BigDecimal.valueOf(1_500.00));
        request.setCurrency("USD");
        request.setTransactionTimestamp(transactionTime);
        request.setLatitude(18.5204);
        request.setLongitude(73.8567);

        TransactionEntity result = transactionMapper.toEntity(request);

        assertAll(
                () -> assertEquals("TXN-1001", result.getTransactionId()),
                () -> assertEquals(10L, result.getUserId()),
                () -> assertEquals(20L, result.getMerchantId()),
                () -> assertEquals(BigDecimal.valueOf(1_500.00), result.getAmount()),
                () -> assertEquals("USD", result.getCurrency()),
                () -> assertEquals(transactionTime, result.getTransactionTimestamp()),
                () -> assertEquals(18.5204, result.getLatitude()),
                () -> assertEquals(73.8567, result.getLongitude()),
                () -> assertEquals(TransactionStatus.PENDING, result.getStatus())
        );
    }

    @Test
    @DisplayName("Should map transaction entity to response")
    void shouldMapTransactionEntityToResponse() {

        LocalDateTime transactionTime = LocalDateTime.now();

        TransactionEntity entity = new TransactionEntity();
        entity.setTransactionId("TXN-1001");
        entity.setUserId(10L);
        entity.setMerchantId(20L);
        entity.setAmount(BigDecimal.valueOf(1_000.71));
        entity.setCurrency("USD");
        entity.setStatus(TransactionStatus.PENDING);
        entity.setTransactionTimestamp(transactionTime);

        TransactionResponse response = transactionMapper.toResponse(entity);

        assertAll(
                () -> assertEquals("TXN-1001", response.getTransactionId()),
                () -> assertEquals(10L, response.getUserId()),
                () -> assertEquals(20L, response.getMerchantId()),
                () -> assertEquals(BigDecimal.valueOf(1_000.71), response.getAmount()),
                () -> assertEquals("USD", response.getCurrency()),
                () -> assertEquals(TransactionStatus.PENDING, response.getStatus()),
                () -> assertEquals(transactionTime, response.getTransactionTimestamp())
        );
    }
}