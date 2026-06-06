package com.sentinelai.platform.fraud.dto;

import java.util.Map;

public class FraudMetricsResponse {

    private long totalTransactions;
    private long approvedTransactions;
    private long flaggedTransactions;
    private long totalFraudAlerts;
    private Map<String, Long> ruleTriggerCounts;

    public long getTotalTransactions() {
        return totalTransactions;
    }

    public void setTotalTransactions(long totalTransactions) {
        this.totalTransactions = totalTransactions;
    }

    public long getApprovedTransactions() {
        return approvedTransactions;
    }

    public void setApprovedTransactions(long approvedTransactions) {
        this.approvedTransactions = approvedTransactions;
    }

    public long getFlaggedTransactions() {
        return flaggedTransactions;
    }

    public void setFlaggedTransactions(long flaggedTransactions) {
        this.flaggedTransactions = flaggedTransactions;
    }

    public long getTotalFraudAlerts() {
        return totalFraudAlerts;
    }

    public void setTotalFraudAlerts(long totalFraudAlerts) {
        this.totalFraudAlerts = totalFraudAlerts;
    }

    public Map<String, Long> getRuleTriggerCounts() {
        return ruleTriggerCounts;
    }

    public void setRuleTriggerCounts(Map<String, Long> ruleTriggerCounts) {
        this.ruleTriggerCounts = ruleTriggerCounts;
    }
}