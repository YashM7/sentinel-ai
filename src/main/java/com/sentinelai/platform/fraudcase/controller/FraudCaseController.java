package com.sentinelai.platform.fraudcase.controller;

import com.sentinelai.platform.fraudcase.dto.FraudCaseResponse;
import com.sentinelai.platform.fraudcase.dto.UpdateFraudCaseStatusRequest;
import com.sentinelai.platform.fraudcase.service.FraudCaseService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/fraud-cases")
public class FraudCaseController {

    private final FraudCaseService fraudCaseService;

    public FraudCaseController(FraudCaseService fraudCaseService) {
        this.fraudCaseService = fraudCaseService;
    }

    @GetMapping
    public List<FraudCaseResponse> getAllCases() {
        return fraudCaseService.getAllCases();
    }

    @GetMapping("/{caseNumber}")
    public FraudCaseResponse getCase(@PathVariable String caseNumber) {
        return fraudCaseService.getCaseByCaseNumber(caseNumber);
    }

    @PatchMapping("/{caseNumber}/status")
    public FraudCaseResponse updateCaseStatus(
            @PathVariable String caseNumber,
            @Valid @RequestBody UpdateFraudCaseStatusRequest request) {
        return fraudCaseService.updateCaseStatus(caseNumber, request.getStatus());
    }
}