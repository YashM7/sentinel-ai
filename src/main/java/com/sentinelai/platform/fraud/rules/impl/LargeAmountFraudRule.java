package com.sentinelai.platform.fraud.rules.impl;

import com.sentinelai.platform.fraud.rules.FraudRule;
import com.sentinelai.platform.fraud.rules.FraudRuleResult;
import com.sentinelai.platform.transaction.entity.TransactionEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class LargeAmountFraudRule implements FraudRule {

    private static final BigDecimal LARGE_AMOUNT_THRESHOLD =
            BigDecimal.valueOf(10_000);

    @Override
    public FraudRuleResult evaluate(TransactionEntity transaction) {

        boolean fraudulent =
                transaction.getAmount()
                        .compareTo(LARGE_AMOUNT_THRESHOLD) > 0;

        if(fraudulent) {
            return new FraudRuleResult(
                    true,
                    getRuleName(),
                    "Transaction amount exceeds fraud threshold"

            );
        }

        return new FraudRuleResult(false, getRuleName(), "Transaction amount is within safe limits");
    }

    @Override
    public String getRuleName() {
        return "LargeAmountFraudRule";
    }
}