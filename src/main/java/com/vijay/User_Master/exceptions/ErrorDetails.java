package com.vijay.User_Master.exceptions;

import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ErrorDetails {
    private HttpStatus responseStatus; // HTTP Status Code
    private String status; // "error" or "failure"
    private String errorMessage; // descriptive error message // Error message
    private Object details; // Additional error details
    private LocalDateTime timestamp; // Error timestamp

    public ResponseEntity<?> create() {
        Map<String, Object> errorMap = new LinkedHashMap<>();
        errorMap.put("status", status);
        errorMap.put("errorMessage", errorMessage);
        errorMap.put("timestamp", timestamp == null ? LocalDateTime.now() : timestamp);

        if (!ObjectUtils.isEmpty(details)) {
            errorMap.put("details", details);
        }
        return new ResponseEntity<>(errorMap, responseStatus);
    }
}

