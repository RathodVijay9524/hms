package com.vijay.User_Master.controller;

import com.vijay.User_Master.Helper.ExceptionUtil;

import com.vijay.User_Master.dto.*;

import com.vijay.User_Master.exceptions.exception.InvalidTokenException;
import com.vijay.User_Master.exceptions.exception.TokenExpiredException;
import com.vijay.User_Master.exceptions.exception.TokenNotFoundException;

import com.vijay.User_Master.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/tokens")
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenController {

    private final RefreshTokenService tokenRefreshService;

    @PostMapping("/regenerate-token")
    public ResponseEntity<?> regenerateToken(@RequestBody RefreshTokenRequest request) {
        try {
            JwtResponse response = tokenRefreshService.refreshAccessToken(request);
            return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);

        } catch (TokenExpiredException e) {
            log.warn("Token expired: {}", e.getMessage());
            return ExceptionUtil.createErrorResponseMessage("Token expired. Please login again", HttpStatus.UNAUTHORIZED);

        } catch (InvalidTokenException e) {
            log.warn("Invalid token: {}", e.getMessage());
            return ExceptionUtil.createErrorResponseMessage("Invalid token", HttpStatus.FORBIDDEN);

        } catch (TokenNotFoundException e) {
            log.warn("Token not found: {}", e.getMessage());
            return ExceptionUtil.createErrorResponseMessage("Token not found", HttpStatus.NOT_FOUND);

        } catch (Exception e) {
            log.error("Token regeneration failed", e);
            return ExceptionUtil.createErrorResponseMessage("Token regeneration failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/invalidate")
    public ResponseEntity<?> invalidateToken(@RequestBody RefreshTokenRequest request) {
        try {
            tokenRefreshService.invalidateRefreshToken(request.getRefreshToken());
            return ExceptionUtil.createBuildResponseMessage("Token invalidated successfully", HttpStatus.OK);

        } catch (TokenNotFoundException e) {
            return ExceptionUtil.createErrorResponseMessage("Token not found", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyToken(@RequestBody RefreshTokenDto refreshTokenDto) {
        try {
            RefreshTokenDto verified = tokenRefreshService.verifyRefreshToken(refreshTokenDto);
            return ExceptionUtil.createBuildResponse(verified, HttpStatus.OK);

        } catch (TokenExpiredException e) {
            return ExceptionUtil.createErrorResponseMessage("Token expired", HttpStatus.UNAUTHORIZED);

        } catch (InvalidTokenException e) {
            return ExceptionUtil.createErrorResponseMessage("Invalid token", HttpStatus.BAD_REQUEST);

        } catch (TokenNotFoundException e) {
            return ExceptionUtil.createErrorResponseMessage("Token not found", HttpStatus.NOT_FOUND);
        }
    }
}
