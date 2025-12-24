package com.vijay.User_Master.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JwtResponse {
    private String jwtToken;
    private UserResponse user;
    private WorkerResponse worker;
    private RefreshTokenDto refreshTokenDto; // Ensure this is of type RefreshTokenDto
    private Object principal; // Add this field

    // If you want to keep backward compatibility
    public Object getPrincipal() {
        if (principal != null) return principal;
        return user != null ? user : worker;
    }
}

