// GlobalExceptionHandler.java
package com.local.train.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleResourceNotFoundException(
            ResourceNotFoundException exception, WebRequest webRequest) {
        
        ErrorDetails errorDetails = ErrorDetails.builder()
                .timestamp(LocalDateTime.now())
                .message(exception.getMessage())
                .path(webRequest.getDescription(false))
                .errorCode("RESOURCE_NOT_FOUND")
                .build();
        
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(InsufficientSeatsException.class)
    public ResponseEntity<ErrorDetails> handleInsufficientSeatsException(
            InsufficientSeatsException exception, WebRequest webRequest) {
        
        ErrorDetails errorDetails = ErrorDetails.builder()
                .timestamp(LocalDateTime.now())
                .message(exception.getMessage())
                .path(webRequest.getDescription(false))
                .errorCode("INSUFFICIENT_SEATS")
                .build();
        
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(PaymentFailedException.class)
    public ResponseEntity<ErrorDetails> handlePaymentFailedException(
            PaymentFailedException exception, WebRequest webRequest) {
        
        ErrorDetails errorDetails = ErrorDetails.builder()
                .timestamp(LocalDateTime.now())
                .message(exception.getMessage())
                .path(webRequest.getDescription(false))
                .errorCode("PAYMENT_FAILED")
                .build();
        
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(CancellationException.class)
    public ResponseEntity<ErrorDetails> handleCancellationException(
            CancellationException exception, WebRequest webRequest) {
        
        ErrorDetails errorDetails = ErrorDetails.builder()
                .timestamp(LocalDateTime.now())
                .message(exception.getMessage())
                .path(webRequest.getDescription(false))
                .errorCode("CANCELLATION_NOT_ALLOWED")
                .build();
        
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorDetails> handleBadCredentialsException(
            BadCredentialsException exception, WebRequest webRequest) {
        
        ErrorDetails errorDetails = ErrorDetails.builder()
                .timestamp(LocalDateTime.now())
                .message("Invalid email or password")
                .path(webRequest.getDescription(false))
                .errorCode("BAD_CREDENTIALS")
                .build();
        
        return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
    }
    
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleUsernameNotFoundException(
            UsernameNotFoundException exception, WebRequest webRequest) {
        
        ErrorDetails errorDetails = ErrorDetails.builder()
                .timestamp(LocalDateTime.now())
                .message(exception.getMessage())
                .path(webRequest.getDescription(false))
                .errorCode("USER_NOT_FOUND")
                .build();
        
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorDetails> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest webRequest) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ValidationErrorDetails errorDetails = ValidationErrorDetails.builder()
                .timestamp(LocalDateTime.now())
                .message("Validation failed")
                .path(webRequest.getDescription(false))
                .errorCode("VALIDATION_FAILED")
                .errors(errors)
                .build();
        
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGlobalException(
            Exception exception, WebRequest webRequest) {
        
        ErrorDetails errorDetails = ErrorDetails.builder()
                .timestamp(LocalDateTime.now())
                .message(exception.getMessage())
                .path(webRequest.getDescription(false))
                .errorCode("INTERNAL_SERVER_ERROR")
                .build();
        
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}