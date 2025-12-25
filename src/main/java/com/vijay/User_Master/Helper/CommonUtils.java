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
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                return null;
            }
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof CustomUserDetails) {
                return (CustomUserDetails) principal;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public static String getUrl(HttpServletRequest request) {
        String apiUrl = request.getRequestURL().toString(); // http:localhost:8080/api/v1/auth
        apiUrl = apiUrl.replace(request.getServletPath(), ""); // http:localhost:8080
        return apiUrl;
    }
}
