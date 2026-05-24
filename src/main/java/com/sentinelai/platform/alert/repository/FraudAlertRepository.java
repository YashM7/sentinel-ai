package com.sentinelai.platform.alert.repository;

import com.sentinelai.platform.alert.entity.FraudAlertEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FraudAlertRepository extends JpaRepository<FraudAlertEntity,Long> {
}
