package com.minimaxi.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // RuntimeException — زي "Machine not found", "Invalid email or password"
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        String message = ex.getMessage();
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        // نحدد الـ status بناءً على الـ message
        if (message != null) {
            if (message.contains("not found")) {
                status = HttpStatus.NOT_FOUND;
            } else if (message.contains("Invalid email or password")) {
                status = HttpStatus.UNAUTHORIZED;
            } else if (message.contains("already exists")) {
                status = HttpStatus.CONFLICT;
            }
        }

        return ResponseEntity.status(status).body(Map.of(
                "status", status.value(),
                "message", message != null ? message : "An error occurred"
        ));
    }

    // General Exception — أي error غير متوقع
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "status", 500,
                "message", "Internal server error"
        ));
    }
}