package com.vijay.User_Master.Helper;



import com.vijay.User_Master.exceptions.ErrorDetails;
import com.vijay.User_Master.exceptions.GenericResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;


@Log4j2
public class ExceptionUtil {

    // Generic success response
    public static <T> ResponseEntity<T> createGenericResponse(T body, HttpStatus status) {
        return new ResponseEntity<>(body, status);
    }

    public static ResponseEntity<?> createBuildResponse(Object data, HttpStatus status) {
        GenericResponse response = GenericResponse.builder()
                .responseStatus(status)
                .status("success")
                .message("success")
                .data(data)
                .build();
        return createGenericResponse(response, status);
    }

    // Success response with a custom message
    public static ResponseEntity<?> createBuildResponseMessage(String message, HttpStatus status) {
        GenericResponse response = GenericResponse.builder()
                .responseStatus(status)
                .status("success")
                .message(message)
                .build();
        return createGenericResponse(response, status);
    }

    // Error response with details
    public static ResponseEntity<?> createErrorResponse(Object details, HttpStatus status) {
        ErrorDetails response = ErrorDetails.builder()
                .responseStatus(status)
                .status("failed")
                .errorMessage("An error occurred..!!")
                .details(details)
                .timestamp(LocalDateTime.now())
                .build();
        return createGenericResponse(response, status);
    }

    // Error response with a custom error message
    public static ResponseEntity<?> createErrorResponseMessage(String errorMessage, HttpStatus status) {
        ErrorDetails response = ErrorDetails.builder()
                .responseStatus(status)
                .status("failed")
                .errorMessage(errorMessage)
                .timestamp(LocalDateTime.now())
                .build();
        return createGenericResponse(response, status);
    }


}

















