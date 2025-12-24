package com.vijay.User_Master.exceptions;

import com.vijay.User_Master.Helper.ExceptionUtil;
import com.vijay.User_Master.exceptions.exception.InvalidTokenException;
import com.vijay.User_Master.exceptions.exception.TokenExpiredException;
import com.vijay.User_Master.exceptions.exception.TokenNotFoundException;
import com.vijay.User_Master.exceptions.exception.TokenRefreshException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ============= TOKEN EXCEPTIONS ============= //
    @ExceptionHandler(TokenNotFoundException.class)
    public ResponseEntity<?> handleTokenNotFoundException(TokenNotFoundException ex) {
        logger.warn("Token not found: {}", ex.getMessage());
        return ExceptionUtil.createErrorResponseMessage(
                "Authentication token not found",
                HttpStatus.NOT_FOUND
        );
    }

    // ===== Token Refresh Specific Exceptions =====
    @ExceptionHandler(TokenRefreshException.class)
    public ResponseEntity<?> handleTokenRefreshException(TokenRefreshException ex) {
        logger.error("Token refresh failed: {}", ex.getMessage());
        return ExceptionUtil.createErrorResponseMessage(
                "Token refresh failed. Please login again",
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<?> handleTokenExpiredException(TokenExpiredException ex) {
        logger.warn("Token expired: {}", ex.getMessage());
        return ExceptionUtil.createErrorResponseMessage(
                "Session expired. Please login again",
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<?> handleInvalidTokenException(InvalidTokenException ex) {
        logger.warn("Invalid token: {}", ex.getMessage());
        return ExceptionUtil.createErrorResponseMessage(
                "Invalid authentication token",
                HttpStatus.FORBIDDEN
        );
    }

    // ============= EXISTING EXCEPTIONS ============= //
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentialsException(BadCredentialsException ex) {
        logger.error("Bad credentials: {}", ex.getMessage());
        return ExceptionUtil.createErrorResponseMessage(
                "Invalid username or password",
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<?> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        logger.error("User exists: {}", ex.getMessage());
        return ExceptionUtil.createErrorResponseMessage(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFound(ResourceNotFoundException ex) {
        logger.error("Resource missing: {}", ex.getMessage());
        return ExceptionUtil.createErrorResponseMessage(
                ex.getMessage(),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex) {
        logger.error("Validation error: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        return ExceptionUtil.createErrorResponse(
                errors,
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception ex) {
        logger.error("System error: {}", ex.getMessage(), ex);
        return ExceptionUtil.createErrorResponseMessage(
                "An unexpected error occurred",
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<?> handleNullPointerException(NullPointerException ex) {
        logger.error("Null pointer: {}", ex.getMessage(), ex);
        return ExceptionUtil.createErrorResponseMessage(
                "A system error occurred",
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(BadApiRequestException.class)
    public ResponseEntity<?> handleBadApiRequest(BadApiRequestException ex) {
        logger.error("Bad API request: {}", ex.getMessage());
        return ExceptionUtil.createErrorResponseMessage(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST
        );
    }
}


