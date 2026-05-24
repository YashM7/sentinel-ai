package com.sentinelai.platform.fraud.rules;

import com.sentinelai.platform.transaction.entity.TransactionEntity;

public interface FraudRule {

    FraudRuleResult evaluate(TransactionEntity transaction);

    String getRuleName();
}
