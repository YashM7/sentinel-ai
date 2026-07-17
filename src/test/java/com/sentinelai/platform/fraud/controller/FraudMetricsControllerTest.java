package com.sentinelai.platform.fraud.controller;

import com.sentinelai.platform.fraud.dto.FraudMetricsResponse;
import com.sentinelai.platform.fraud.service.FraudMetricsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(FraudMetricsController.class)
public class FraudMetricsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FraudMetricsService fraudMetricsService;

    @Test
    @DisplayName("Should return fraud metrics when metrics endpoint is called")
    void shouldReturnFraudMetrics() throws Exception {

        FraudMetricsResponse response = new FraudMetricsResponse();

        response.setTotalTransactions(100L);
        response.setApprovedTransactions(70L);
        response.setFlaggedTransactions(30L);
        response.setTotalFraudAlerts(30L);
        response.setRuleTriggerCounts(
                Map.of(
                        "LargeAmountFraudRule", 20L,
                        "VelocityFraudRule", 10L
                )
        );

        Mockito.when(
                fraudMetricsService.getFraudMetrics()
        ).thenReturn(response);

        mockMvc.perform(
                get("/api/v1/fraud/metrics")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalTransactions").value(100))
                .andExpect(jsonPath("$.approvedTransactions").value(70))
                .andExpect(jsonPath("$.flaggedTransactions").value(30))
                .andExpect(jsonPath("$.totalFraudAlerts").value(30))
                .andExpect(jsonPath("$.ruleTriggerCounts.LargeAmountFraudRule").value(20))
                .andExpect(jsonPath("$.ruleTriggerCounts.VelocityFraudRule").value(10));

        Mockito.verify(fraudMetricsService).getFraudMetrics();
    }
}