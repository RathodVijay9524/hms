package com.vijay.User_Master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRoleRequest {
    private Long userId;
    private Set<Long> roleIds;
    private String action; // "ASSIGN", "REMOVE", "REPLACE"
}
