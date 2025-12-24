package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.*;

public interface RefreshTokenService {

    JwtResponse refreshAccessToken(RefreshTokenRequest request);

    RefreshTokenDto createRefreshToken(String username, String email, Long userId, Long workerId);

    RefreshTokenDto verifyRefreshToken(RefreshTokenDto refreshTokenDto);

    void invalidateRefreshToken(String token);




    //create
    /*RefreshTokenDto createRefreshToken(String username,String email);

    // find by token
    RefreshTokenDto findByToken(String token);
//verify



    UserResponse getUser(RefreshTokenDto dto);

    WorkerResponse getWorker(RefreshTokenDto verifiedRefreshToken);

    boolean isWorkerToken(RefreshTokenDto refreshToken);
    boolean isUserToken(RefreshTokenDto refreshToken);*/
}
