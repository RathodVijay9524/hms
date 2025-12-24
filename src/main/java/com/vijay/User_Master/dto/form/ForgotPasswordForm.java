package com.vijay.User_Master.dto.form;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ForgotPasswordForm {
    private String newPassword;
    private String confirmPassword;
}
