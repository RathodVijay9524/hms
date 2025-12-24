package com.vijay.User_Master.dto;

import com.vijay.User_Master.entity.Role;
import com.vijay.User_Master.entity.User;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
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
    private Set<Role> roles;
    private AccountStatusResponse accountStatus;
}
