package com.sentinelai.platform.fraudcase.controller;

import com.sentinelai.platform.fraudcase.dto.FraudCaseResponse;
import com.sentinelai.platform.fraudcase.service.FraudCaseService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}