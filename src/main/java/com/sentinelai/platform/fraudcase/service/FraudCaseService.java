package com.sentinelai.platform.fraudcase.service;

import com.sentinelai.platform.common.exception.FraudCaseNotFoundException;
import com.sentinelai.platform.fraudcase.dto.FraudCaseResponse;
import com.sentinelai.platform.fraudcase.entity.FraudCaseEntity;
import com.sentinelai.platform.fraudcase.entity.FraudCaseStatus;
import com.sentinelai.platform.fraudcase.repository.FraudCaseRepository;
import com.sentinelai.platform.transaction.entity.TransactionEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class FraudCaseService {

    private final FraudCaseRepository fraudCaseRepository;

    public FraudCaseService(FraudCaseRepository fraudCaseRepository) {
        this.fraudCaseRepository = fraudCaseRepository;
    }

    public FraudCaseEntity createFraudCase(TransactionEntity transaction) {
        FraudCaseEntity fraudCase = new FraudCaseEntity();

        String caseNumber =
                "CASE-" +
                        LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE) +
                        "-" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0,8)
                                .toUpperCase();

        fraudCase.setCaseNumber(caseNumber);
        fraudCase.setStatus(FraudCaseStatus.OPEN);
        LocalDateTime now = LocalDateTime.now();
        fraudCase.setCreatedAt(now);
        fraudCase.setUpdatedAt(now);
        fraudCase.setTransaction(transaction);

        return fraudCaseRepository.save(fraudCase);
    }

    public List<FraudCaseResponse> getAllCases() {
        return fraudCaseRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public FraudCaseResponse getCaseByCaseNumber(String caseNumber) {
        return fraudCaseRepository.findByCaseNumber(caseNumber)
                .map(this::toResponse)
                .orElseThrow(() ->
                        new FraudCaseNotFoundException("Fraud case not found: " + caseNumber));
    }

    private FraudCaseResponse toResponse(FraudCaseEntity fraudCase) {
        FraudCaseResponse response = new FraudCaseResponse();

        response.setId(fraudCase.getId());
        response.setCaseNumber(fraudCase.getCaseNumber());
        response.setStatus(fraudCase.getStatus());
        response.setTransactionId(fraudCase.getTransaction().getTransactionId());
        response.setCreatedAt(fraudCase.getCreatedAt());
        return response;
    }
}