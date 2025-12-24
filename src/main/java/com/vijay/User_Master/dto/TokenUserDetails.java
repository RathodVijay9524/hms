package com.vijay.User_Master.dto;

import com.vijay.User_Master.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

import lombok.Setter;

import java.util.Set;

@AllArgsConstructor
@Getter
@Setter
public class TokenUserDetails {
    private final String username;
    private final String email;
    private final Long userId;
    private final Long workerId;
    private final Set<Role> roles;
}
