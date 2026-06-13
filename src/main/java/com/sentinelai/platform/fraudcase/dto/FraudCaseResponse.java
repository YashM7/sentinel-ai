package com.sentinelai.platform.fraudcase.dto;

import com.sentinelai.platform.fraudcase.entity.FraudCaseStatus;

import java.time.LocalDateTime;

public class FraudCaseResponse {

    private Long id;
    private String caseNumber;
    private FraudCaseStatus status;
    private String transactionId;
    private LocalDateTime createdAt;

    public FraudCaseResponse() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCaseNumber() {
        return caseNumber;
    }

    public void setCaseNumber(String caseNumber) {
        this.caseNumber = caseNumber;
    }

    public FraudCaseStatus getStatus() {
        return status;
    }

    public void setStatus(FraudCaseStatus status) {
        this.status = status;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}