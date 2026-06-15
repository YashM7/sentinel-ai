package com.sentinelai.platform.fraudcase.repository;

import com.sentinelai.platform.fraudcase.entity.FraudCaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FraudCaseRepository extends JpaRepository<FraudCaseEntity, Long> {

    Optional<FraudCaseEntity> findByCaseNumber(String caseNumber);
}
