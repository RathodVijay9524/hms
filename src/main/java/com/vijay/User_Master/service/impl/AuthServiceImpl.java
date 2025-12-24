package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.Helper.EmailUtils;
import com.vijay.User_Master.config.security.CustomUserDetails;
import com.vijay.User_Master.config.security.JwtTokenProvider;
import com.vijay.User_Master.config.security.model.LoginJWTResponse;
import com.vijay.User_Master.config.security.model.LoginRequest;
import com.vijay.User_Master.dto.RefreshTokenDto;
import com.vijay.User_Master.dto.UserRequest;
import com.vijay.User_Master.dto.UserResponse;
import com.vijay.User_Master.dto.form.*;
import com.vijay.User_Master.entity.AccountStatus;
import com.vijay.User_Master.entity.Role;
import com.vijay.User_Master.entity.User;
import com.vijay.User_Master.entity.Worker;
import com.vijay.User_Master.exceptions.BadApiRequestException;
import com.vijay.User_Master.exceptions.ResourceNotFoundException;
import com.vijay.User_Master.exceptions.UserAlreadyExistsException;
import com.vijay.User_Master.repository.RoleRepository;
import com.vijay.User_Master.repository.UserRepository;
import com.vijay.User_Master.repository.WorkerRepository;
import com.vijay.User_Master.service.AuthService;
import com.vijay.User_Master.service.RefreshTokenService;
import com.vijay.User_Master.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
@Service
@Log4j2
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final WorkerRepository workerRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final ModelMapper mapper;
    private UserDetailsService userDetailsService;
    private JwtTokenProvider jwtTokenProvider;
    private AuthenticationManager authenticationManager;
    private EmailUtils emailUtils;
    private EmailService emailService;
    private final RefreshTokenService refreshTokenService;

    /*
     *     **************  when user register that time need to send temp password
     *                 to unlock account in email then user can create new password and he or see can log in. *****
     *
     *        but in this application we are verifier user direct when he clicks on link... inside mail
     *        so this method implemented for knowledge
     *
     * */
    @Override
    public boolean unlockAccount(UnlockForm form, String usernameOrEmail) {
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> {
                    log.error("User with ID '{}' not found", usernameOrEmail);
                    return new ResourceNotFoundException("USER", "ID", usernameOrEmail);
                });
        // Check if the temporary password matches the user's current password
        if (!passwordEncoder.matches(form.getTempPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Temporary Password is incorrect");
        }
        // Ensure new password and confirm password match
        if (!form.getNewPassword().equals(form.getConfirmPassword())) {
            throw new IllegalArgumentException("New Password and Confirm Password do not match");
        }
        // Encode the new password and save it
        user.setPassword(passwordEncoder.encode(form.getNewPassword()));

        /*
         *   need to set status as unlock or Active, then user can log in
         * */
        AccountStatus accountStatus = AccountStatus.builder()
                .isActive(true)
                .build();
        user.setAccountStatus(accountStatus);

        userRepository.save(user);
        // Send a success email to the user
        String subject = "Congratulations! Your Account is Unlocked";
        String body = "Your account has been successfully unlocked. You can now log in with your new password.<br>Thank you.";
        emailUtils.sendEmail(user.getEmail(), subject, body);
        log.info("Account unlocked successfully for user ID: {}", user.getName());
        return true;
    }

    @Override
    public boolean forgotPassword(ForgotPasswordForm form, String usernameOrEmail) {
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> {
                    log.error("User with ID '{}' not found", usernameOrEmail);
                    return new ResourceNotFoundException("USER", "ID", usernameOrEmail);
                });

        // Check if new password and confirm password match
        if (!form.getNewPassword().equals(form.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        // Encode the new password and save it
        String encodedNewPassword = passwordEncoder.encode(form.getNewPassword());
        user.setPassword(encodedNewPassword);
        userRepository.save(user);

        log.info("Password reset successfully for user ID: {}", user.getId());
        return true;
    }


    @Override
    public boolean changePassword(ChangePasswordForm form) {
        CustomUserDetails userDetails = CommonUtils.getLoggedInUser();
        User user = userRepository.findByEmail(userDetails.getEmail());
        // Log the incoming reset password request
        System.out.println("Resetting password for email: " + user.getEmail());
        // Check if the user exists
        if (user == null) {
            System.out.println("User not found with email: " + user.getEmail());
            return false;
        }
        if (form.getOldPassword() == null || form.getOldPassword().isEmpty()) {
            System.out.println("Old password is null or empty!");
        }
        if (!passwordEncoder.matches(form.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Old Password is incorrect ");
        }
        String encodePasswordNewPassword = passwordEncoder.encode(form.getNewPassword());
        // Check if the old password matches the user's current password
        user.setPassword(encodePasswordNewPassword);
        userRepository.save(user);
        return true;
    }
    @Override
    public boolean existsByUsernameOrEmailFields(String username, String email) {
     return true;
    }
    @Override
    public boolean existsByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.existsByUsername(usernameOrEmail)
                || userRepository.existsByEmail(usernameOrEmail)
                || workerRepository.existsByUsername(usernameOrEmail)
                || workerRepository.existsByEmail(usernameOrEmail);
    }

    @Override
    public LoginJWTResponse login(LoginRequest req) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(req.getUsernameOrEmail(), req.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = userDetailsService.loadUserByUsername(req.getUsernameOrEmail());

        // Check if the input is an email or username
        User user = null;
        Worker worker = null;
        if (isEmail(req.getUsernameOrEmail())) {
            user = userRepository.findByEmail(req.getUsernameOrEmail());
            worker = workerRepository.findByEmail(req.getUsernameOrEmail()).orElse(null);
        } else {
            user = userRepository.findByUsername(req.getUsernameOrEmail());
            worker = workerRepository.findByEmail(req.getUsernameOrEmail()).orElse(null);
        }

        if (worker != null && (worker.getAccountStatus() == null || !worker.getAccountStatus().getIsActive())) {
            log.warn("Account status is null or inactive for worker ID: {}", worker.getId());
            throw new BadApiRequestException("Account is not active. Please activate your account.");
        }
        if (user != null && (user.getAccountStatus() == null || !user.getAccountStatus().getIsActive())) {
            log.warn("Account status is null or inactive for user ID: {}", user.getId());
            throw new BadApiRequestException("Account is not active. Please activate your account.");
        }

        // Create new refresh token
        RefreshTokenDto refreshTokenCreated = null;

        if (user != null) {
            log.info("Creating refresh token for user: {}", user.getUsername());
            refreshTokenCreated = refreshTokenService.createRefreshToken(
                    user.getUsername(),
                    user.getEmail(),
                    user.getId(),   // <-- Pass userId
                    null            // <-- WorkerId is null
            );
        } else if (worker != null) {
            log.info("Creating refresh token for worker: {}", worker.getUsername());
            refreshTokenCreated = refreshTokenService.createRefreshToken(
                    worker.getUsername(),
                    worker.getEmail(),
                    null,           // <-- UserId is null
                    worker.getId()  // <-- Pass workerId
            );
        }

        if (refreshTokenCreated == null) {
            log.error("Error creating refresh token.");
            throw new RuntimeException("Error creating refresh token.");
        }


        String token = jwtTokenProvider.generateToken(authentication);

        UserResponse response = mapper.map(userDetails, UserResponse.class);

        LoginJWTResponse jwtResponse = LoginJWTResponse.builder()
                .jwtToken(token)
                .user(response)
                .refreshTokenDto(refreshTokenCreated)
                .build();
        return jwtResponse;
    }



    private boolean isEmail(String usernameOrEmail) {
        // Simple check to see if the string is an email
        return usernameOrEmail.contains("@");
    }


    @Override
    public CompletableFuture<Object> registerForAdminUser(UserRequest request, String url) {
        log.info("Attempting to create a new admin user with username: {}", request.getUsername());
        return CompletableFuture.supplyAsync(() -> {
            if (existsByUsernameOrEmail(request.getUsername()) || existsByUsernameOrEmail(request.getEmail())) {
                log.error("Username '{}' or email '{}' already exists", request.getUsername(), request.getEmail());
                throw new UserAlreadyExistsException("Username or email is already taken");
            }
            User user = mapper.map(request, User.class);
            Role role = roleRepository.findByName("ROLE_NORMAL").orElseThrow(() -> {
                log.error("Role 'User' not found");
                return new BadApiRequestException("Role not found with name 'ROLE_ADMIN'");
            });
            user.setRoles(Set.of(role));
            //String tempPwd= PwdUtils.generateRandomPwd();
            //user.setPassword(tempPwd);
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            AccountStatus accountStatus = AccountStatus.builder()
                    .isActive(false)
                    .passwordResetToken(null)
                    .verificationCode(UUID.randomUUID().toString())
                    .build();
            user.setAccountStatus(accountStatus);

            User savedUser = userRepository.save(user);

            // Send confirmation email to the new admin user
            if (!ObjectUtils.isEmpty(savedUser)) {
                // send email
                try {
                    emailSendForRegisterUser(savedUser, url);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return true;
            }
            log.info("Admin user with username '{}' created successfully", user.getUsername());
            return mapper.map(user, UserResponse.class);
        });
    }

    private void emailSendForRegisterUser(User savedUser, String url) throws Exception {

        String message = "Hi,<b>[[username]]</b> " + "<br> Your account register sucessfully.<br>"
                + "<br> Click the below link verify & Active your account <br>"
                + "<a href='[[url]]'>Click Here</a> <br><br>" + "Thanks,<br>Enotes.com";

        message = message.replace("[[username]]", savedUser.getName());
       /* message = message.replace("[[url]]", url + "/api/v1/home/verify?uid=" + savedUser.getId() + "&&code="
                + savedUser.getAccountStatus().getVerificationCode());*/

        message = message.replace("[[url]]", "https://codewithvijay.online/verify?uid=" + savedUser.getId()
                + "&code=" + savedUser.getAccountStatus().getVerificationCode());

        EmailForm emailRequest = EmailForm.builder()
                .to(savedUser.getEmail())
                .title("Account Creating Confirmation")
                .subject("Account Created Success")
                .message(message)
                .build();
        emailService.sendEmail(emailRequest);
    }

    @Override
    public UserResponse registerForNormalUser(UserRequest request) {
        log.info("Attempting to create a new normal user with username: {}", request.getUsername());
        if (existsByUsernameOrEmail(request.getUsername()) || existsByUsernameOrEmail(request.getEmail())) {
            log.error("Username '{}' or email '{}' already exists", request.getUsername(), request.getEmail());
            throw new UserAlreadyExistsException("Username or email is already taken");
        }
        UserResponse currentUser = userService.getCurrentUser();
        User user = mapper.map(currentUser, User.class);

        Worker worker = mapper.map(request, Worker.class);
        worker.setPassword(passwordEncoder.encode(request.getPassword()));

        // Fetch the worker role from the repository
        Role workerRole = roleRepository.findByName("ROLE_WORKER")
                .orElseThrow(() -> new BadApiRequestException("Worker role not found."));

        // Initialize the roles field if it's null
        if (worker.getRoles() == null) {
            worker.setRoles(new HashSet<>());
        }
        // Add the worker role to the roles set
        worker.getRoles().add(workerRole);
        worker.setUser(user);
        workerRepository.save(worker);
        return mapper.map(worker, UserResponse.class);
    }

    /*
     *
     *   Password reset by sending mail logic start from here !!
     *
     * */

    @Override
    public void sendEmailPasswordReset(String email, HttpServletRequest request) throws Exception {
        // Fetch user from the database using email
        User user = userRepository.findByEmail(email);
        if (ObjectUtils.isEmpty(user)) {
            log.error("Invalid email...!!!: {}", email);
            throw new BadApiRequestException("Invalid email");
        }

        // Generate unique password reset token
        String passwordResetToken = UUID.randomUUID().toString();
        user.getAccountStatus().setPasswordResetToken(passwordResetToken);
        User updatedUser = userRepository.save(user);

        // Get the base URL of your API (e.g., http://localhost:9091)
        String url = CommonUtils.getUrl(request);

        // Frontend Base URL
        //String frontendBaseUrl = "http://localhost:5173";
        String frontendBaseUrl = "https://codewithvijay.online";

        // Send the email with the reset link
        sendEmailRequest(updatedUser, frontendBaseUrl);
        log.info("Password reset email sent to: {}", email);
    }

    private void sendEmailRequest(User user, String url) throws Exception {
        // Email message template with placeholders
        String message = "Hi <b>[[username]]</b>, "
                + "<br><p>You have requested to reset your password.</p>"
                + "<p>Click the link below to reset your password:</p>"
                + "<p><a href=[[url]]>Reset my password</a></p>"
                + "<p>Ignore this email if you remember your password, "
                + "or you did not make the request.</p><br>"
                + "Thanks,<br>Vijay Rathod";

        // Replace placeholders with actual user details and URL
        message = message.replace("[[username]]", user.getName());
        message = message.replace("[[url]]", url + "/reset-password?uid=" + user.getId() + "&token="
                + user.getAccountStatus().getPasswordResetToken());

        // Create an EmailForm object to represent the email
        EmailForm emailRequest = EmailForm.builder()
                .to(user.getEmail())
                .title("Password Reset")
                .subject("Password Reset Link")
                .message(message)
                .build();

        // Send the password reset email
        emailService.sendEmail(emailRequest);
        log.info("Password reset email sent to user: {}", user.getEmail());
    }


    @Override
    public void verifyPasswordResetLink(Long uid, String code) throws Exception {
        User user = userRepository.findById(uid).orElseThrow(() -> new BadApiRequestException("invalid user"));
        verifyPasswordResetToken(user.getAccountStatus().getPasswordResetToken(), code);
    }

    @Override
    public void verifyAndResetPassword(Long uid, String token, String newPassword, String confirmPassword) throws Exception {
        User user = userRepository.findById(uid)
                .orElseThrow(() -> {
                    log.error("User with ID '{}' not found", uid);
                    return new ResourceNotFoundException("USER", "ID", uid);
                });

        verifyPasswordResetToken(user.getAccountStatus().getPasswordResetToken(), token);

        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        user.getAccountStatus().setPasswordResetToken(null);
        userRepository.save(user);
        log.info("Password reset successfully for user ID: {}", uid);
    }

    private void verifyPasswordResetToken(String existToken, String reqToken) {
        if (!StringUtils.hasText(reqToken)) {
            throw new IllegalArgumentException("Invalid token");
        }
        if (!StringUtils.hasText(existToken)) {
            throw new IllegalArgumentException("Password already reset");
        }
        if (!existToken.equals(reqToken)) {
            throw new IllegalArgumentException("Invalid URL");
        }
    }
}





  /*
          this for Post Request method ,
          it will not show verify success it says post request not support for ths...

          on same link -  we can reset password
 */

/*

    private void sendEmailRequest(User user, String url) throws Exception {
        String message = "Hi <b>[[username]]</b>, "
                + "<br><p>You have requested to reset your password.</p>"
                + "<p>Click the link below to reset your password:</p>"
                + "<p><a href=[[url]]>Reset my password</a></p>"
                + "<p>Ignore this email if you remember your password, "
                + "or you did not make the request.</p><br>"
                + "Thanks,<br>Vijay Rathod";

        message = message.replace("[[username]]", user.getName());
        message = message.replace("[[url]]", url + "/api/v1/home/reset-password?uid=" + user.getId() + "&token="
                + user.getAccountStatus().getPasswordResetToken());

        EmailForm emailRequest = EmailForm.builder()
                .to(user.getEmail())
                .title("Password Reset")
                .subject("Password Reset Link")
                .message(message)
                .build();

        // Send password reset email to user
        emailService.sendEmail(emailRequest);
        log.info("Password reset email sent to user: {}", user.getEmail());
    }


   /*
          this for for get method , verify success shows... but we cant reset password
          on same link - it says get request cannot support
 */

/*
   private void sendEmailRequest(User user, String url) throws Exception {

        String message = "Hi <b>[[username]]</b> "
                + "<br><p>You have requested to reset your password.</p>"
                + "<p>Click the link below to change your password:</p>"
                + "<p><a href=[[url]]>Change my password</a></p>"
                + "<p>Ignore this email if you do remember your password, "
                + "or you have not made the request.</p><br>"
                + "Thanks,<br>  Vijay Rathod";

        message = message.replace("[[username]]", user.getName());
        message = message.replace("[[url]]", url + "/api/v1/home/verify-pswd-link?uid=" + user.getId() + "&&code="
                + user.getAccountStatus().getPasswordResetToken());

        EmailForm emailRequest = EmailForm.builder().to(user.getEmail())
                .title("Password Reset").subject("Password Reset link").message(message).build();
        // send password reset email to user
        emailService.sendEmail(emailRequest);
    }
*/