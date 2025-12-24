package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.config.security.CustomUserDetailsService;
import com.vijay.User_Master.config.security.JwtTokenProvider;
import com.vijay.User_Master.dto.*;
import com.vijay.User_Master.entity.RefreshToken;
import com.vijay.User_Master.exceptions.ResourceNotFoundException;
import com.vijay.User_Master.exceptions.exception.InvalidTokenException;
import com.vijay.User_Master.exceptions.exception.TokenExpiredException;
import com.vijay.User_Master.exceptions.exception.TokenNotFoundException;
import com.vijay.User_Master.repository.RefreshTokenRepository;
import com.vijay.User_Master.repository.UserRepository;
import com.vijay.User_Master.repository.WorkerRepository;
import com.vijay.User_Master.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final WorkerRepository workerRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;
    private final ModelMapper modelMapper;

    private static final long REFRESH_TOKEN_EXPIRATION_DAYS = 7;


    @Override
    public JwtResponse refreshAccessToken(RefreshTokenRequest request) {
        RefreshTokenDto verifiedToken = verifyRefreshToken(
                RefreshTokenDto.builder().refreshToken(request.getRefreshToken()).build());

        TokenUserDetails userDetails = resolveTokenUserDetails(verifiedToken);

        String newAccessToken = generateAccessToken(userDetails);
        RefreshTokenDto newRefreshToken = createRefreshToken(
                userDetails.getUsername(),
                userDetails.getEmail(),
                userDetails.getUserId(),
                userDetails.getWorkerId()
        );

        return buildJwtResponse(newAccessToken, newRefreshToken, userDetails);
    }

    @Override
    public RefreshTokenDto createRefreshToken(String username, String email, Long userId, Long workerId) {
        log.info("Creating refresh token for: {}", username);

        if (userId != null) {
            refreshTokenRepository.deleteByUserId(userId);
        } else if (workerId != null) {
            refreshTokenRepository.deleteByWorkerId(workerId);
        }

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusSeconds(REFRESH_TOKEN_EXPIRATION_DAYS * 24 * 60 * 60))
                .username(username)
                .email(email)
                .user(userId != null ? userRepository.getReferenceById(userId) : null)
                .worker(workerId != null ? workerRepository.getReferenceById(workerId) : null)
                .build();

        return modelMapper.map(refreshTokenRepository.save(refreshToken), RefreshTokenDto.class);

    }

    @Override
    @Transactional(readOnly = true)
    public RefreshTokenDto verifyRefreshToken(RefreshTokenDto refreshTokenDto) {
        if (refreshTokenDto == null || refreshTokenDto.getRefreshToken() == null) {
            throw new TokenNotFoundException("Refresh token is null");
        }

        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenDto.getRefreshToken())
                .orElseThrow(() -> new TokenNotFoundException("Refresh token not found"));

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new TokenExpiredException("Refresh token expired");
        }

        if (!(refreshToken.getUser() != null ^ refreshToken.getWorker() != null)) {
            throw new InvalidTokenException("Invalid token type configuration");
        }

        return modelMapper.map(refreshToken, RefreshTokenDto.class);
    }

    @Override
    @Transactional
    public void invalidateRefreshToken(String token) {
        refreshTokenRepository.findByToken(token)
                .ifPresentOrElse(
                        refreshTokenRepository::delete,
                        () -> {
                            throw new TokenNotFoundException("Token not found");
                        }
                );
    }

    private TokenUserDetails resolveTokenUserDetails(RefreshTokenDto verifiedToken) {
        if (verifiedToken.getUserId() != null) {
            return userRepository.findById(verifiedToken.getUserId())
                    .map(user -> new TokenUserDetails(
                            user.getUsername(),
                            user.getEmail(),
                            user.getId(),
                            null,
                            user.getRoles()
                    ))
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", verifiedToken.getUserId()));
        } else {
            return workerRepository.findById(verifiedToken.getWorkerId())
                    .map(worker -> new TokenUserDetails(
                            worker.getUsername(),
                            worker.getEmail(),
                            null,
                            worker.getId(),
                            worker.getRoles()
                    ))
                    .orElseThrow(() -> new ResourceNotFoundException("Worker", "id", verifiedToken.getWorkerId()));
        }
    }

    private String generateAccessToken(TokenUserDetails userDetails) {
        UserDetails springUserDetails = customUserDetailsService.loadUserByUsername(userDetails.getUsername());
        return jwtTokenProvider.generateToken(
                new UsernamePasswordAuthenticationToken(
                        springUserDetails,
                        null,
                        springUserDetails.getAuthorities()
                )
        );
    }

    private JwtResponse buildJwtResponse(String accessToken, RefreshTokenDto refreshToken, TokenUserDetails userDetails) {
        return JwtResponse.builder()
                .jwtToken(accessToken)
                .refreshTokenDto(refreshToken)
                .principal(buildPrincipalResponse(userDetails))
                .build();
    }

    private Object buildPrincipalResponse(TokenUserDetails userDetails) {
        if (userDetails.getUserId() != null) {
            return modelMapper.map(
                    userRepository.findById(userDetails.getUserId())
                            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userDetails.getUserId())),
                    UserResponse.class
            );
        } else {
            return modelMapper.map(
                    workerRepository.findById(userDetails.getWorkerId())
                            .orElseThrow(() -> new ResourceNotFoundException("Worker", "id", userDetails.getWorkerId())),
                    WorkerResponse.class
            );
        }
    }
}
