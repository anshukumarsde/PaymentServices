package com.example.payments.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        return error(HttpStatus.BAD_REQUEST, messageOrDefault(ex.getMessage(), "Invalid request."));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleResponseStatus(ResponseStatusException ex) {
        String message = messageOrDefault(ex.getReason(), "Request failed.");
        return ResponseEntity.status(ex.getStatusCode()).body(Map.of("error", message));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleUnreadableMessage(HttpMessageNotReadableException ex) {
        return error(HttpStatus.BAD_REQUEST, "Request body is missing or invalid.");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        return error(HttpStatus.CONFLICT, "The request conflicts with the current data.");
    }

    private ResponseEntity<Map<String, String>> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of("error", message));
    }

    private String messageOrDefault(String message, String fallback) {
        return message == null || message.isBlank() ? fallback : message;
    }
}
