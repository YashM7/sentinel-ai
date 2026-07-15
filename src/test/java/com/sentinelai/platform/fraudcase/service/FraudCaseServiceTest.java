package com.sentinelai.platform.fraudcase.service;

import com.sentinelai.platform.common.exception.FraudCaseNotFoundException;
import com.sentinelai.platform.common.exception.InvalidFraudCaseStatusTransitionException;
import com.sentinelai.platform.fraudcase.dto.FraudCaseResponse;
import com.sentinelai.platform.fraudcase.entity.FraudCaseEntity;
import com.sentinelai.platform.fraudcase.entity.FraudCaseStatus;
import com.sentinelai.platform.fraudcase.repository.FraudCaseRepository;
import com.sentinelai.platform.transaction.entity.TransactionEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class FraudCaseServiceTest {

    @Mock
    private FraudCaseRepository fraudCaseRepository;

    private FraudCaseService fraudCaseService;

    @BeforeEach
    void setup() {
        fraudCaseService = new FraudCaseService(fraudCaseRepository);
    }

    @Test
    @DisplayName("Should create fraud case with OPEN status and save it")
    void shouldCreateFraudCaseWithOpenStatusAndSaveIt() {

        TransactionEntity transaction = new TransactionEntity();
        transaction.setTransactionId("TXN-1001");

        Mockito.when(
                fraudCaseRepository.save(Mockito.any(FraudCaseEntity.class))
        ).thenAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<FraudCaseEntity> captor =
                ArgumentCaptor.forClass(FraudCaseEntity.class);

        FraudCaseEntity result =
                fraudCaseService.createFraudCase(transaction);

        Mockito.verify(fraudCaseRepository).save(captor.capture());

        FraudCaseEntity savedFraudCase = captor.getValue();

        assertAll(
                () -> assertNotNull(savedFraudCase.getCaseNumber()),
                () -> assertTrue(
                        savedFraudCase.getCaseNumber()
                                .matches("CASE-\\d{8}-[A-Z0-9]{8}")
                ),
                () -> assertEquals(
                        FraudCaseStatus.OPEN,
                        savedFraudCase.getStatus()
                ),
                () -> assertNotNull(savedFraudCase.getCreatedAt()),
                () -> assertNotNull(savedFraudCase.getUpdatedAt()),
                () -> assertEquals(
                        savedFraudCase.getCreatedAt(),
                        savedFraudCase.getUpdatedAt()
                ),
                () -> assertSame(
                        transaction,
                        savedFraudCase.getTransaction()
                ),
                () -> assertSame(
                        savedFraudCase,
                        result
                )
        );
    }

    @Test
    @DisplayName("Should throw exception when repository save fails")
    void shouldThrowExceptionWhenRepositorySaveFails() {

        TransactionEntity transaction = new TransactionEntity();
        transaction.setTransactionId("TXN-1001");

        Mockito.when(
                fraudCaseRepository.save(Mockito.any(FraudCaseEntity.class))
        ).thenThrow(new RuntimeException("Database error"));

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> fraudCaseService.createFraudCase(transaction)
                );

        assertEquals(
                "Database error",
                exception.getMessage()
        );

        Mockito.verify(
                fraudCaseRepository,
                Mockito.times(1)
        ).save(Mockito.any(FraudCaseEntity.class));
    }

    @Test
    @DisplayName("Should return empty list when no fraud cases exist")
    void shouldReturnEmptyListWhenNoFraudCasesExist() {

        Mockito.when(
                fraudCaseRepository.findAll()
        ).thenReturn(List.of());

        List<FraudCaseResponse> responses =
                fraudCaseService.getAllCases();

        assertTrue(responses.isEmpty());

        Mockito.verify(fraudCaseRepository).findAll();
    }

    @Test
    @DisplayName("Should return multiple fraud cases when cases exist")
    void shouldReturnMultipleFraudCasesWhenCasesExist() {

        LocalDateTime createdAt = LocalDateTime.now();

        FraudCaseEntity firstCase = new FraudCaseEntity();
        TransactionEntity firstTransaction = new TransactionEntity();
        firstTransaction.setTransactionId("TXN-1001");

        firstCase.setId(1L);
        firstCase.setCaseNumber("CASE-20260713-D0259C04");
        firstCase.setStatus(FraudCaseStatus.CONFIRMED_FRAUD);
        firstCase.setCreatedAt(createdAt);
        firstCase.setTransaction(firstTransaction);

        FraudCaseEntity secondCase = new FraudCaseEntity();
        TransactionEntity secondTransaction = new TransactionEntity();
        secondTransaction.setTransactionId("TXN-1002");

        secondCase.setId(2L);
        secondCase.setCaseNumber("CASE-20260713-X5259V34");
        secondCase.setStatus(FraudCaseStatus.OPEN);
        secondCase.setCreatedAt(createdAt);
        secondCase.setTransaction(secondTransaction);

        Mockito.when(
                fraudCaseRepository.findAll()
        ).thenReturn(List.of(firstCase, secondCase));

        List<FraudCaseResponse> response =
                fraudCaseService.getAllCases();

        assertAll(
                () -> assertEquals(2, response.size()),
                () -> assertEquals(
                        1L,
                        response.get(0).getId()
                ),
                () -> assertEquals(
                        "CASE-20260713-D0259C04",
                        response.get(0).getCaseNumber()
                ),
                () -> assertEquals(
                        FraudCaseStatus.CONFIRMED_FRAUD,
                        response.get(0).getStatus()
                ),
                () -> assertEquals(
                        "TXN-1001",
                        response.get(0).getTransactionId()
                ),
                () -> assertEquals(
                        createdAt,
                        response.get(0).getCreatedAt()
                ),
                () -> assertEquals(
                        2L,
                        response.get(1).getId()
                ),
                () -> assertEquals(
                        "CASE-20260713-X5259V34",
                        response.get(1).getCaseNumber()
                ),
                () -> assertEquals(
                        FraudCaseStatus.OPEN,
                        response.get(1).getStatus()
                ),
                () -> assertEquals(
                        "TXN-1002",
                        response.get(1).getTransactionId()
                ),
                () -> assertEquals(
                        createdAt,
                        response.get(1).getCreatedAt()
                )
        );

        Mockito.verify(fraudCaseRepository).findAll();
    }

    @Test
    @DisplayName("Should return fraud case when case number exists")
    void shouldReturnFraudCaseWhenCaseNumberExists() {

        String caseNumber = "CASE-20260713-X5259V34";
        String transactionId = "TXN-1001";
        LocalDateTime createdAt = LocalDateTime.now();

        TransactionEntity transaction = new TransactionEntity();
        transaction.setTransactionId(transactionId);

        FraudCaseEntity fraudCase = new FraudCaseEntity();
        fraudCase.setId(1L);
        fraudCase.setCaseNumber(caseNumber);
        fraudCase.setStatus(FraudCaseStatus.OPEN);
        fraudCase.setCreatedAt(createdAt);
        fraudCase.setTransaction(transaction);

        Mockito.when(
                fraudCaseRepository.findByCaseNumber(caseNumber)
        ).thenReturn(Optional.of(fraudCase));

        FraudCaseResponse response =
                fraudCaseService.getCaseByCaseNumber(caseNumber);

        assertAll(
                () -> assertEquals(1L, response.getId()),
                () -> assertEquals(caseNumber, response.getCaseNumber()),
                () -> assertEquals(FraudCaseStatus.OPEN, response.getStatus()),
                () -> assertEquals(transactionId, response.getTransactionId()),
                () -> assertEquals(createdAt, response.getCreatedAt())
        );

        Mockito.verify(fraudCaseRepository)
                .findByCaseNumber(caseNumber);
    }

    @Test
    @DisplayName("Should throw FraudCaseNotFoundException when case number does not exist")
    void shouldThrowFraudCaseNotFoundExceptionWhenCaseNumberDoesNotExist() {

        String caseNumber = "CASE-20260713-X5259V34";

        Mockito.when(
                fraudCaseRepository.findByCaseNumber(caseNumber)
        ).thenReturn(Optional.empty());

        FraudCaseNotFoundException exception =
                assertThrows(
                        FraudCaseNotFoundException.class,
                        () -> fraudCaseService.getCaseByCaseNumber(caseNumber)
                );

        assertEquals(
                "Fraud case not found: " + caseNumber,
                exception.getMessage()
        );

        Mockito.verify(fraudCaseRepository).findByCaseNumber(caseNumber);
    }

    @Test
    @DisplayName("Should update fraud case status when transition is valid")
    void shouldUpdateStatusWhenTransitionIsValid() {

        TransactionEntity transaction = new TransactionEntity();
        transaction.setTransactionId("TXN-1001");

        FraudCaseEntity fraudCase = new FraudCaseEntity();
        fraudCase.setCaseNumber("CASE-20260713-D0259C04");
        fraudCase.setStatus(FraudCaseStatus.OPEN);
        fraudCase.setTransaction(transaction);

        Mockito.when(
                fraudCaseRepository.findByCaseNumber("CASE-20260713-D0259C04")
        ).thenReturn(Optional.of(fraudCase));

        Mockito.when(
                fraudCaseRepository.save(fraudCase)
        ).thenReturn(fraudCase);

        FraudCaseResponse response =
                fraudCaseService.updateCaseStatus(
                        "CASE-20260713-D0259C04",
                        FraudCaseStatus.UNDER_REVIEW
                );

        assertEquals(
                FraudCaseStatus.UNDER_REVIEW,
                response.getStatus()
        );

        Mockito.verify(fraudCaseRepository).save(fraudCase);
    }

    @Test
    @DisplayName("Should throw exception when fraud case status transition is invalid")
    void shouldThrowExceptionWhenStatusTransitionIsInvalid() {

        TransactionEntity transaction = new TransactionEntity();
        transaction.setTransactionId("TXN-1001");

        FraudCaseEntity fraudCase = new FraudCaseEntity();
        fraudCase.setCaseNumber("CASE-20260713-D0259C04");
        fraudCase.setStatus(FraudCaseStatus.OPEN);
        fraudCase.setTransaction(transaction);

        Mockito.when(
                fraudCaseRepository.findByCaseNumber("CASE-20260713-D0259C04")
        ).thenReturn(Optional.of(fraudCase));

        assertThrows(
                InvalidFraudCaseStatusTransitionException.class,
                () -> fraudCaseService.updateCaseStatus(
                        "CASE-20260713-D0259C04",
                        FraudCaseStatus.CLOSED
                )
        );

        Mockito.verify(
                fraudCaseRepository,
                Mockito.never()
        ).save(Mockito.any());
    }
}