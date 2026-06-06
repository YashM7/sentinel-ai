package com.sentinelai.platform.fraud.controller;

import com.sentinelai.platform.fraud.dto.FraudMetricsResponse;
import com.sentinelai.platform.fraud.service.FraudMetricsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/fraud")
public class FraudMetricsController {

    private final FraudMetricsService fraudMetricsService;

    public FraudMetricsController(FraudMetricsService fraudMetricsService) {
        this.fraudMetricsService = fraudMetricsService;
    }

    @GetMapping("/metrics")
    public FraudMetricsResponse getFraudMetrics() {
        return fraudMetricsService.getFraudMetrics();
    }
}