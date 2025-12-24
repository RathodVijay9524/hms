package com.vijay.User_Master.dto.form;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
 public class ChangePasswordForm {
    private String oldPassword;
    private String newPassword;
}

