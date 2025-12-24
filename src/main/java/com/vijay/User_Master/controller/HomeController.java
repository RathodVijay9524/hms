package com.vijay.User_Master.controller;

import com.vijay.User_Master.Helper.ExceptionUtil;
import com.vijay.User_Master.dto.form.ForgotPasswordForm;
import com.vijay.User_Master.dto.form.PasswordResetForm;
import com.vijay.User_Master.dto.form.UnlockForm;
import com.vijay.User_Master.exceptions.ResourceNotFoundException;
import com.vijay.User_Master.service.AuthService;
import com.vijay.User_Master.service.HomeService;
import com.vijay.User_Master.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.websocket.server.PathParam;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/home")
@AllArgsConstructor
public class HomeController {


    private HomeService homeService;
    private AuthService authService;

    @GetMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyUserAccount(@RequestParam Long uid, @RequestParam String code) throws Exception {
        boolean verified = homeService.verifyAccount(uid, code);

        Map<String, Object> response = new HashMap<>();
        if (verified) {
            response.put("status", "success");
            response.put("message", "Account verified successfully!");
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "error");
            response.put("message", "Invalid or expired verification link.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // you can reset password using email or username
    //http://localhost:9091/api/v1/home/forgot-password?usernameOrEmail=vijayrathod9524@gmail.com
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordForm form, @RequestParam String usernameOrEmail) {
        try {
            authService.forgotPassword(form, usernameOrEmail);
            return ExceptionUtil.createBuildResponseMessage("Password reset successfully", HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return ExceptionUtil.createErrorResponseMessage(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return ExceptionUtil.createErrorResponseMessage(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return ExceptionUtil.createErrorResponseMessage("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // you can reset password using email or username
    // if he or see gets temp password on mail then ,user can create new password using temp password
    //http://localhost:9091/api/v1/home/unlock-account?usernameOrEmail=vijayrathod9524@gmail.com
    @PostMapping("/unlock-account")
    public ResponseEntity<?> unlockAccount(@RequestBody UnlockForm form, @RequestParam String usernameOrEmail) {
        try {
            boolean result = authService.unlockAccount(form, usernameOrEmail);
            if (result) {
                return ExceptionUtil.createBuildResponseMessage("Account unlocked successfully", HttpStatus.OK);
            } else {
                return ExceptionUtil.createErrorResponseMessage("Failed to unlock account", HttpStatus.BAD_REQUEST);
            }
        } catch (ResourceNotFoundException e) {
            return ExceptionUtil.createErrorResponseMessage(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return ExceptionUtil.createErrorResponseMessage(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return ExceptionUtil.createErrorResponseMessage("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /*
    *     ********   Password reset logic by sending mail  ***************
    *
    * */

    // http://localhost:9091/api/v1/home/send-email-reset?email=vijayrathod9524@gmail.com
    @GetMapping("/send-email-reset")
    public ResponseEntity<?> sendEmailForPasswordReset(@RequestParam String email, HttpServletRequest request)
            throws Exception {
        authService.sendEmailPasswordReset(email, request);
        return ExceptionUtil.createBuildResponseMessage("Email Send Success !! Check Email Reset Password", HttpStatus.OK);
    }

    // http://localhost:9091/api/v1/home/reset-password?uid=10&token=51e7d5ad-928f-4fbc-b3ba-b8ca99bb1271
    @GetMapping("/verify-pswd-link")
    public ResponseEntity<?> verifyPasswordResetLink(@RequestParam Long uid, @RequestParam String code)
            throws Exception {
        authService.verifyPasswordResetLink(uid, code);
        return ExceptionUtil.createBuildResponseMessage("verification success", HttpStatus.OK);
    }

    // http://localhost:9091/api/v1/home/reset-password?uid=10&token=51e7d5ad-928f-4fbc-b3ba-b8ca99bb1271
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPasswords(@RequestBody PasswordResetForm form) {
        try {
            authService.verifyAndResetPassword(form.getUid(), form.getToken(), form.getNewPassword(), form.getConfirmPassword());
            return ExceptionUtil.createBuildResponseMessage("Password reset successfully", HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return ExceptionUtil.createErrorResponseMessage(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return ExceptionUtil.createErrorResponseMessage(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return ExceptionUtil.createErrorResponseMessage("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /*
    *    ********* welcome message for application *****************
    *
    * */
    @GetMapping("/welcome")
    public String hi() {
        return "Welcome to java Programing";
    }

    /*
     *    ********* CSRF Token User for application *****************
     *
     * */

    @GetMapping("/csrf")
    public CsrfToken getCsrfToken(HttpServletRequest request) {
        /*
         * This endpoint retrieves the CSRF token from the request attributes.
         *
         * CSRF tokens are used to prevent Cross-Site Request Forgery attacks by ensuring
         * that state-changing requests (POST, PUT, DELETE, etc.) originate from the
         * authenticated client.
         *
         * The token is fetched from the "_csrf" attribute, set by Spring Security, and
         * returned to the client so it can include the token in headers or request bodies
         * when making state-changing API calls.
         */
        return (CsrfToken) request.getAttribute("_csrf");
    }
}




