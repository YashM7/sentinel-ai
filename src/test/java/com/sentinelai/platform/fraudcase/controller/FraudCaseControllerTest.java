package com.sentinelai.platform.fraudcase.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sentinelai.platform.common.exception.FraudCaseNotFoundException;
import com.sentinelai.platform.common.exception.InvalidFraudCaseStatusTransitionException;
import com.sentinelai.platform.fraudcase.dto.FraudCaseResponse;
import com.sentinelai.platform.fraudcase.dto.UpdateFraudCaseStatusRequest;
import com.sentinelai.platform.fraudcase.entity.FraudCaseStatus;
import com.sentinelai.platform.fraudcase.service.FraudCaseService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(FraudCaseController.class)
public class FraudCaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FraudCaseService fraudCaseService;

    @Test
    @DisplayName("Should return all fraud cases")
    void shouldReturnAllFraudCases() throws Exception {

        LocalDateTime now = LocalDateTime.now();

        FraudCaseResponse firstCase = new FraudCaseResponse();
        firstCase.setId(1L);
        firstCase.setCaseNumber("CASE-20260713-D0259C04");
        firstCase.setStatus(FraudCaseStatus.CLOSED);
        firstCase.setTransactionId("TXN-1001");
        firstCase.setCreatedAt(now);

        FraudCaseResponse secondCase = new FraudCaseResponse();
        secondCase.setId(2L);
        secondCase.setCaseNumber("CASE-20260713-X5259V34");
        secondCase.setStatus(FraudCaseStatus.OPEN);
        secondCase.setTransactionId("TXN-1002");
        secondCase.setCreatedAt(now);


        Mockito.when(
                fraudCaseService.getAllCases()
        ).thenReturn(List.of(firstCase, secondCase));

        mockMvc.perform(
                get("/api/v1/fraud-cases")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].caseNumber").value("CASE-20260713-D0259C04"))
                .andExpect(jsonPath("$[0].status").value("CLOSED"))
                .andExpect(jsonPath("$[0].transactionId").value("TXN-1001"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].caseNumber").value("CASE-20260713-X5259V34"))
                .andExpect(jsonPath("$[1].status").value("OPEN"))
                .andExpect(jsonPath("$[1].transactionId").value("TXN-1002"));

        Mockito.verify(fraudCaseService).getAllCases();
    }

    @Test
    @DisplayName("Should return empty list when no fraud cases exist")
    void shouldReturnEmptyListWhenNoCasesExist() throws Exception {

        Mockito.when(
                fraudCaseService.getAllCases()
        ).thenReturn(List.of());

        mockMvc.perform(
                get("/api/v1/fraud-cases")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        Mockito.verify(fraudCaseService).getAllCases();
    }

    @Test
    @DisplayName("Should return fraud case when case number exists")
    void shouldReturnFraudCaseWhenCaseNumberExists() throws Exception {

        String caseNumber = "CASE-20260713-D0259C04";

        FraudCaseResponse response = new FraudCaseResponse();
        response.setId(1L);
        response.setCaseNumber(caseNumber);
        response.setStatus(FraudCaseStatus.CLOSED);
        response.setTransactionId("TXN-1001");
        response.setCreatedAt(LocalDateTime.now());

        Mockito.when(
                fraudCaseService.getCaseByCaseNumber(caseNumber)
        ).thenReturn(response);

        mockMvc.perform(
                get("/api/v1/fraud-cases/{caseNumber}", caseNumber)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.caseNumber").value(caseNumber))
                .andExpect(jsonPath("$.status").value("CLOSED"))
                .andExpect(jsonPath("$.transactionId").value("TXN-1001"));

        Mockito.verify(fraudCaseService).getCaseByCaseNumber(caseNumber);
    }

    @Test
    @DisplayName("Should return 404 when case number does not exist")
    void shouldReturn404WhenCaseNumberDoesNotExist() throws Exception {

        String caseNumber = "CASE-20260716-Unknown";

        Mockito.when(
                fraudCaseService.getCaseByCaseNumber(caseNumber)
        ).thenThrow(
                new FraudCaseNotFoundException("Fraud case not found: " + caseNumber)
        );

        mockMvc.perform(
                get("/api/v1/fraud-cases/{caseNumber}", caseNumber)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Fraud case not found: " + caseNumber));

        Mockito.verify(fraudCaseService).getCaseByCaseNumber(caseNumber);
    }

    @Test
    @DisplayName("Should update fraud case status")
    void shouldUpdateFraudCaseStatus() throws Exception {

        UpdateFraudCaseStatusRequest request = new UpdateFraudCaseStatusRequest();
        request.setStatus(FraudCaseStatus.UNDER_REVIEW);

        String requestBody = objectMapper.writeValueAsString(request);

        String caseNumber = "CASE-20260713-D0259C04";

        FraudCaseResponse response = new FraudCaseResponse();
        response.setId(1L);
        response.setCaseNumber(caseNumber);
        response.setStatus(FraudCaseStatus.UNDER_REVIEW);
        response.setTransactionId("TXN-1001");
        response.setCreatedAt(LocalDateTime.now());

        Mockito.when(
                fraudCaseService.updateCaseStatus(caseNumber, FraudCaseStatus.UNDER_REVIEW)
        ).thenReturn(response);

        mockMvc.perform(
                patch("/api/v1/fraud-cases/{caseNumber}/status", caseNumber)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.caseNumber").value(caseNumber))
                .andExpect(jsonPath("$.status").value("UNDER_REVIEW"))
                .andExpect(jsonPath("$.transactionId").value("TXN-1001"));

        Mockito.verify(fraudCaseService).updateCaseStatus(caseNumber, FraudCaseStatus.UNDER_REVIEW);
    }

    @Test
    @DisplayName("Should return 400 Bad Request when status is null")
    void shouldReturn400BadRequestWhenStatusIsNull() throws Exception {

        UpdateFraudCaseStatusRequest request = new UpdateFraudCaseStatusRequest();
        request.setStatus(null);

        String requestBody = objectMapper.writeValueAsString(request);

        String caseNumber = "CASE-20260713-D0259C04";

        mockMvc.perform(
                patch("/api/v1/fraud-cases/{caseNumber}/status", caseNumber)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                )
                .andExpect(status().isBadRequest());

        Mockito.verify(fraudCaseService, Mockito.never())
                .updateCaseStatus(Mockito.anyString(), Mockito.any());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when status transition is invalid")
    void shouldReturn400BadRequestWhenStatusTransitionIsInvalid() throws Exception {

        UpdateFraudCaseStatusRequest request = new UpdateFraudCaseStatusRequest();
        request.setStatus(FraudCaseStatus.OPEN);

        String requestBody = objectMapper.writeValueAsString(request);

        String caseNumber = "CASE-20260713-D0259C04";

        Mockito.when(
                fraudCaseService.updateCaseStatus(caseNumber, FraudCaseStatus.OPEN)
        ).thenThrow(
                new InvalidFraudCaseStatusTransitionException("Invalid status transition from OPEN to OPEN")
        );

        mockMvc.perform(
                patch("/api/v1/fraud-cases/{caseNumber}/status", caseNumber)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                )
                .andExpect(status().isBadRequest());

        Mockito.verify(fraudCaseService).updateCaseStatus(caseNumber, FraudCaseStatus.OPEN);
    }

    @Test
    @DisplayName("Should return 404 Not Found when fraud case does not exist")
    void shouldReturn404NotFoundWhenFraudCaseDoesNotExist() throws Exception {

        UpdateFraudCaseStatusRequest request = new UpdateFraudCaseStatusRequest();
        request.setStatus(FraudCaseStatus.OPEN);

        String requestBody = objectMapper.writeValueAsString(request);

        String caseNumber = "CASE-20260717-Unknown";

        Mockito.when(
                fraudCaseService.updateCaseStatus(caseNumber, FraudCaseStatus.OPEN)
        ).thenThrow(
                new FraudCaseNotFoundException("Fraud case not found: " + caseNumber)
        );

        mockMvc.perform(
                        patch("/api/v1/fraud-cases/{caseNumber}/status", caseNumber)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isNotFound());

        Mockito.verify(fraudCaseService).updateCaseStatus(caseNumber, FraudCaseStatus.OPEN);
    }
}