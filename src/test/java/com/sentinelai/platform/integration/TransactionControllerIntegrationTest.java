package com.sentinelai.platform.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sentinelai.platform.alert.repository.FraudAlertRepository;
import com.sentinelai.platform.audit.repository.AuditLogRepository;
import com.sentinelai.platform.fraudcase.entity.FraudCaseEntity;
import com.sentinelai.platform.fraudcase.entity.FraudCaseStatus;
import com.sentinelai.platform.fraudcase.repository.FraudCaseRepository;
import com.sentinelai.platform.transaction.dto.request.CreateTransactionRequest;
import com.sentinelai.platform.transaction.entity.TransactionEntity;
import com.sentinelai.platform.transaction.entity.TransactionStatus;
import com.sentinelai.platform.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.containsString;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class TransactionControllerIntegrationTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    private final TransactionRepository transactionRepository;
    private final FraudAlertRepository fraudAlertRepository;
    private final AuditLogRepository auditLogRepository;
    private final FraudCaseRepository fraudCaseRepository;


    public TransactionControllerIntegrationTest(
            MockMvc mockMvc,
            ObjectMapper objectMapper,
            TransactionRepository transactionRepository,
            FraudAlertRepository fraudAlertRepository,
            AuditLogRepository auditLogRepository,
            FraudCaseRepository fraudCaseRepository)
    {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.transactionRepository = transactionRepository;
        this.fraudAlertRepository = fraudAlertRepository;
        this.auditLogRepository = auditLogRepository;
        this.fraudCaseRepository = fraudCaseRepository;
    }

    @Test
    @DisplayName("Should create fraud artifacts when transaction triggers fraud rule")
    void createFraudArtifactsWhenTransactionTriggersFraudRule() throws Exception {

        CreateTransactionRequest request = new CreateTransactionRequest();
        request.setTransactionId("TXN-1001");
        request.setUserId(1L);
        request.setMerchantId(100L);
        request.setAmount(new BigDecimal("15000.76"));
        request.setCurrency("USD");
        request.setTransactionTimestamp(LocalDateTime.now());

        mockMvc.perform(
                post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transactionId").value("TXN-1001"))
                .andExpect(jsonPath("$.status").value(TransactionStatus.FLAGGED.name()));

        assertThat(transactionRepository.existsByTransactionId("TXN-1001"))
                .isTrue();

        TransactionEntity transaction =
                transactionRepository.findByTransactionId("TXN-1001").orElseThrow();

        assertThat(transaction.getStatus())
                .isEqualTo(TransactionStatus.FLAGGED);

        assertThat(fraudAlertRepository.count())
                .isEqualTo(1);

        FraudCaseEntity fraudCase = fraudCaseRepository.findAll().get(0);

        assertThat(fraudCase.getCaseNumber())
                .isNotBlank();

        assertThat(fraudCase.getStatus())
                .isEqualTo(FraudCaseStatus.OPEN);

        assertThat(fraudCase.getTransaction().getTransactionId())
                .isEqualTo("TXN-1001");

        assertThat(auditLogRepository.count())
                .isEqualTo(1);

    }

    @Test
    @DisplayName("Should approve transaction without creating fraud artifacts")
    void approveTransactionWithoutCreatingFraudArtifacts() throws Exception {

        CreateTransactionRequest request = new CreateTransactionRequest();
        request.setTransactionId("TXN-1002");
        request.setUserId(1L);
        request.setMerchantId(100L);
        request.setAmount(new BigDecimal("150.76"));
        request.setCurrency("USD");
        request.setTransactionTimestamp(LocalDateTime.now());

        mockMvc.perform(
                post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transactionId").value("TXN-1002"))
                .andExpect(jsonPath("$.status").value(TransactionStatus.APPROVED.name()));

        assertThat(transactionRepository.existsByTransactionId("TXN-1002"))
                .isTrue();

        TransactionEntity transaction =
                transactionRepository.findByTransactionId("TXN-1002").orElseThrow();

        assertThat(transaction.getStatus())
                .isEqualTo(TransactionStatus.APPROVED);

        assertThat(fraudAlertRepository.count())
                .isZero();

        assertThat(fraudCaseRepository.count())
                .isZero();

        assertThat(auditLogRepository.count())
                .isEqualTo(1);
    }

    @Test
    @DisplayName("Should reject duplicate transaction without creating additional artifacts")
    void rejectDuplicateTransactionWithoutCreatingAdditionalArtifacts() throws Exception {

        CreateTransactionRequest request = new CreateTransactionRequest();
        request.setTransactionId("TXN-1003");
        request.setUserId(1L);
        request.setMerchantId(100L);
        request.setAmount(new BigDecimal("150.00"));
        request.setCurrency("USD");
        request.setTransactionTimestamp(LocalDateTime.now());

        mockMvc.perform(
                post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated());

        mockMvc.perform(
                post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value(containsString("TXN-1003")))
                .andExpect(jsonPath("$.path").value("/api/v1/transactions"));

        assertThat(transactionRepository.count())
                .isEqualTo(1);

        assertThat(fraudAlertRepository.count())
                .isZero();

        assertThat(fraudCaseRepository.count())
                .isZero();

        assertThat(auditLogRepository.count())
                .isEqualTo(1);
    }

    @Test
    @DisplayName("Should return bad request when transaction request is invalid")
    void returnBadRequestWhenTransactionRequestIsInvalid() throws Exception {

        CreateTransactionRequest request = new CreateTransactionRequest();
        request.setTransactionId("");
        request.setUserId(1L);
        request.setMerchantId(100L);
        request.setAmount(new BigDecimal("150.00"));
        request.setCurrency("USD");
        request.setTransactionTimestamp(LocalDateTime.now());

        mockMvc.perform(
                post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value(containsString("transactionId is required")))
                .andExpect(jsonPath("$.path").value("/api/v1/transactions"));

        assertThat(transactionRepository.count())
                .isZero();

        assertThat(fraudAlertRepository.count())
                .isZero();

        assertThat(fraudCaseRepository.count())
                .isZero();

        assertThat(auditLogRepository.count())
                .isZero();
    }
}