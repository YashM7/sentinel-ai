package com.sentinelai.platform.transaction.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sentinelai.platform.transaction.dto.request.CreateTransactionRequest;
import com.sentinelai.platform.transaction.dto.response.TransactionResponse;
import com.sentinelai.platform.transaction.service.TransactionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TransactionService transactionService;

    @Test
    @DisplayName("Should create transaction successfully when payload is valid")
    void shouldCreateTransactionSuccessfully() throws Exception {

        CreateTransactionRequest request = new CreateTransactionRequest();
        request.setTransactionId("TXN-1001");
        request.setUserId(1L);
        request.setMerchantId(100L);
        request.setAmount(new BigDecimal("1500.76"));
        request.setCurrency("USD");
        request.setTransactionTimestamp(LocalDateTime.now());

        String json = objectMapper.writeValueAsString(request);

        TransactionResponse response = new TransactionResponse();
        response.setTransactionId("TXN-1001");
        response.setUserId(1L);
        response.setMerchantId(100L);
        response.setAmount(new BigDecimal("1500.76"));
        response.setCurrency("USD");

        Mockito.when(
                transactionService.createTransaction(Mockito.any())
        ).thenReturn(response);

        mockMvc.perform(
                post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transactionId").value("TXN-1001"))
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.merchantId").value(100L))
                .andExpect(jsonPath("$.amount").value(1500.76))
                .andExpect(jsonPath("$.currency").value("USD"));

        Mockito.verify(transactionService)
                .createTransaction(Mockito.any());
    }

    @Test
    @DisplayName("Should return 400 bad request when validation fails")
    void shouldReturnBadRequestWhenPayloadIsInvalid() throws Exception {

        CreateTransactionRequest invalidRequest = new CreateTransactionRequest();

        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(transactionService);
    }
}