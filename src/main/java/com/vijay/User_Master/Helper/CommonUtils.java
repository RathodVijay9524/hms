package com.vijay.User_Master.Helper;

import com.vijay.User_Master.config.security.CustomUserDetails;
import com.vijay.User_Master.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;

public class CommonUtils {

    @Transactional
    public static CustomUserDetails getLoggedInUser() {
        try {
            CustomUserDetails logUser = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return logUser;
        } catch (Exception e) {
            throw new RuntimeException("User is not authenticated.", e);
        }
    }

    public static String getUrl(HttpServletRequest request) {
        String apiUrl = request.getRequestURL().toString(); // http:localhost:8080/api/v1/auth
        apiUrl = apiUrl.replace(request.getServletPath(), ""); // http:localhost:8080
        return apiUrl;
    }
}
