package com.sentinelai.platform.transaction.mapper;

import com.sentinelai.platform.transaction.dto.request.CreateTransactionRequest;
import com.sentinelai.platform.transaction.dto.response.TransactionResponse;
import com.sentinelai.platform.transaction.entity.TransactionEntity;
import com.sentinelai.platform.transaction.entity.TransactionStatus;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public TransactionEntity toEntity(CreateTransactionRequest request) {

        TransactionEntity entity = new TransactionEntity();

        entity.setTransactionId(request.getTransactionId());
        entity.setUserId(request.getUserId());
        entity.setMerchantId(request.getMerchantId());
        entity.setAmount(request.getAmount());
        entity.setCurrency(request.getCurrency());
        entity.setTransactionTimestamp(request.getTransactionTimestamp());
        entity.setLatitude(request.getLatitude());
        entity.setLongitude(request.getLongitude());

        entity.setStatus(TransactionStatus.PENDING);

        return entity;
    }

    public TransactionResponse toResponse(TransactionEntity entity) {

        TransactionResponse response = new TransactionResponse();

        response.setTransactionId(entity.getTransactionId());
        response.setUserId(entity.getUserId());
        response.setMerchantId(entity.getMerchantId());
        response.setAmount(entity.getAmount());
        response.setCurrency(entity.getCurrency());
        response.setStatus(entity.getStatus());
        response.setTransactionTimestamp(entity.getTransactionTimestamp());
        response.setCreatedAt(entity.getCreatedAt());

        return response;
    }
}