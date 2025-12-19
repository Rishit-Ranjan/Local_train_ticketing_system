package com.local.train.exception;

public class CancellationException extends RuntimeException {
    public CancellationException(String message) {
        super(message);
    }
}