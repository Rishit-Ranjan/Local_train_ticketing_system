// Custom Exceptions
package com.local.train.exception;









public class ScheduleConflictException extends RuntimeException {
    public ScheduleConflictException(String message) {
        super(message);
    }
}