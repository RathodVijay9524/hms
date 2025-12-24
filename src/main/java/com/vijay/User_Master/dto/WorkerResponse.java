package com.vijay.User_Master.dto;

import com.vijay.User_Master.entity.Role;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkerResponse {
    private Long id;
    private String name;

    private String username;

    private String email;
    private String password;
    private String phoNo;

    private String about;


    private String imageName;
    private boolean isDeleted;
    private LocalDateTime deletedOn;
    private Set<Role> roles; // Roles are strings like "ROLE_ADMIN"
    private AccountStatus accountStatus;

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Getter
    @Setter
    public static class RoleDto {
        private Long id;
        private String name;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Getter
    @Setter
    public static class AccountStatus {
        private Long id;
        private Boolean isActive;
    }

}
