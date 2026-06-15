package com.minimaxi.backend.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalState(IllegalStateException ex) {
        String message = ex.getMessage();

        if (message != null && message.startsWith("MACHINE_HAS_DEPENDENCIES:")) {
            String[] parts = message.split(":");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "status", 409,
                    "code", "MACHINE_HAS_DEPENDENCIES",
                    "message", "Cannot delete machine: it has related work orders and/or issues.",
                    "workOrdersCount", Integer.parseInt(parts[1]),
                    "issuesCount", Integer.parseInt(parts[2])
            ));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "status", 400,
                "message", message
        ));
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "status", 400,
                "message", "Data integrity violation: " + ex.getMostSpecificCause().getMessage()
              /*  "status", 400,
                "message" , "Can not delete entity because it is referenced by other entities. Please remove related entities first."*/
        ));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        String message = ex.getMessage();
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "status", 500,
                "message", "Internal server error"
        ));
    }
}