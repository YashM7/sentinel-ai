package com.sentinelai.platform.fraudcase.repository;

import com.sentinelai.platform.fraudcase.entity.FraudCaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FraudCaseRepository extends JpaRepository<FraudCaseEntity, Long> {
}
