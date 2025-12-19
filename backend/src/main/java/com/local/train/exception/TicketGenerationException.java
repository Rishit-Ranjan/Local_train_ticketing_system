package com.local.train.exception;

public class TicketGenerationException extends RuntimeException {
    public TicketGenerationException(String message) {
        super(message);
    }
}