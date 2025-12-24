package com.vijay.User_Master.service;

import com.vijay.User_Master.config.security.model.LoginJWTResponse;
import com.vijay.User_Master.config.security.model.LoginRequest;
import com.vijay.User_Master.dto.UserRequest;
import com.vijay.User_Master.dto.UserResponse;

import com.vijay.User_Master.dto.form.ChangePasswordForm;

import com.vijay.User_Master.dto.form.ForgotPasswordForm;
import com.vijay.User_Master.dto.form.PasswordResetForm;
import com.vijay.User_Master.dto.form.UnlockForm;
import jakarta.servlet.http.HttpServletRequest;

import java.util.concurrent.CompletableFuture;

public interface AuthService {

    boolean unlockAccount(UnlockForm form, String usernameOrEmail);
    // For  password

    boolean forgotPassword(ForgotPasswordForm from, String email);
    boolean changePassword(ChangePasswordForm form);
    boolean existsByUsernameOrEmail(String usernameOrEmail);
    boolean existsByUsernameOrEmailFields(String username, String email);
    LoginJWTResponse login(LoginRequest req);
    CompletableFuture<Object> registerForAdminUser(UserRequest request,String url);
    UserResponse registerForNormalUser(UserRequest request);
    /*
    *            Rest Password by sending mail !
    * */

    public void sendEmailPasswordReset(String email, HttpServletRequest request) throws Exception;

    public void verifyPasswordResetLink(Long uid, String code) throws Exception;

    public void verifyAndResetPassword(Long uid, String token, String newPassword, String confirmPassword) throws Exception;

}
