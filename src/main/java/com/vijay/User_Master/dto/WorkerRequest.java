package com.vijay.User_Master.dto;

import com.vijay.User_Master.entity.AccountStatus;
import com.vijay.User_Master.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkerRequest {

    private String name;
    private String username;
    private String email;
    private String password;
    private String phoNo;
    private String about;
    private String imageName;
    private boolean isDeleted;
    private LocalDateTime deletedOn;
    private Set<String> roles; // Roles are strings like "ROLE_ADMIN"
    private User user;
    private AccountStatus accountStatus;
}
