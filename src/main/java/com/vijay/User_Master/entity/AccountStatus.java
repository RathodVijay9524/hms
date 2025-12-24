package com.vijay.User_Master.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
public class AccountStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Indicates whether the account is active or not
    private Boolean isActive;

    // Code used for verifying the account during registration
    private String verificationCode;

    // Token used for password reset via email link
    private String passwordResetToken;
}


/*
 *   for reset password by otp with mail link, send otp,
 *   by clicking on link get front-end side page you can enter opt and submit
 *   if verification is success
 *   then you can navigate to next page like reset password -
 *   then you can enter newPassword and confirm password
 *
 * */

