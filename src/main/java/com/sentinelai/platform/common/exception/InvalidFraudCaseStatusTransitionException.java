package com.sentinelai.platform.common.exception;

public class InvalidFraudCaseStatusTransitionException extends RuntimeException {

    public InvalidFraudCaseStatusTransitionException (String message) {
        super(message);
    }
}
