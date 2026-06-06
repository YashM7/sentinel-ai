package com.sentinelai.platform.alert.repository;

import com.sentinelai.platform.alert.entity.FraudAlertEntity;
import com.sentinelai.platform.alert.repository.projection.RuleTriggerCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FraudAlertRepository extends JpaRepository<FraudAlertEntity,Long> {

    @Query("""
            SELECT
                f.ruleName as ruleName,
                COUNT(f) as count
            FROM FraudAlertEntity f
            GROUP BY f.ruleName
            """)
    List<RuleTriggerCount> countAlertsByRule();
}
