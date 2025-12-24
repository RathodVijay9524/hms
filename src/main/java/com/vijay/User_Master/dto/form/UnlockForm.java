package com.vijay.User_Master.dto.form;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UnlockForm {
    private String tempPassword;
    private String newPassword;
    private String confirmPassword;
}

