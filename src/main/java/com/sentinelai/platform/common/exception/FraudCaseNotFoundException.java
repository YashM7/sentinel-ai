package com.sentinelai.platform.common.exception;

public class FraudCaseNotFoundException extends RuntimeException{

    public FraudCaseNotFoundException (String message) {
        super(message);
    }
}