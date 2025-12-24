package com.vijay.User_Master.dto.form;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PasswordResetForm {
    private Long uid;
    private String token;
    private String newPassword;
    private String confirmPassword;
}
