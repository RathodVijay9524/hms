package com.vijay.User_Master.config.security.model;

import com.vijay.User_Master.dto.RefreshTokenDto;
import com.vijay.User_Master.dto.UserResponse;

import com.vijay.User_Master.entity.Role;
import com.vijay.User_Master.entity.User;
import lombok.*;

import java.util.Set;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginJWTResponse {

    private String jwtToken;
    private UserResponse user;
    private RefreshTokenDto refreshTokenDto;
}
