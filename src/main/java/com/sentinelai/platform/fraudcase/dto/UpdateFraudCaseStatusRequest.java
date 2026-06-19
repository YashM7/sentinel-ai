package com.sentinelai.platform.fraudcase.dto;

import com.sentinelai.platform.fraudcase.entity.FraudCaseStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateFraudCaseStatusRequest {
    @NotNull(message = "Status must not be null")
    private FraudCaseStatus status;

    public UpdateFraudCaseStatusRequest() {

    }

    public FraudCaseStatus getStatus() {
        return status;
    }

    public void setStatus(FraudCaseStatus status) {
        this.status = status;
    }
}