package com.vijay.User_Master.config.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        
        String redirectUrl = "/dashboard";
        
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        boolean isAdmin = authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        
        if (authorities.size() > 1 || isAdmin) {
            response.sendRedirect("/select-role");
            return;
        }

        String role = authorities.iterator().next().getAuthority();
            
            if (role.equals("ROLE_ADMIN")) {
                redirectUrl = "/admin/dashboard";
            } else if (role.equals("ROLE_DOCTOR")) {
                redirectUrl = "/doctor/dashboard";
            } else if (role.equals("ROLE_RECEPTIONIST")) {
                redirectUrl = "/reception/dashboard";
            } else if (role.equals("ROLE_LAB_TECHNICIAN")) {
                redirectUrl = "/lab/dashboard";
            } else if (role.equals("ROLE_PATIENT")) {
                redirectUrl = "/patient/dashboard";
            } else if (role.equals("ROLE_BILLING") || role.equals("ROLE_ACCOUNTANT")) {
                redirectUrl = "/billing/dashboard";
            } else if (role.equals("ROLE_OWNER")) {
                redirectUrl = "/owner/dashboard";
            } else if (role.equals("ROLE_PHARMACIST")) {
                redirectUrl = "/pharmacy/dashboard";
            } else if (role.equals("ROLE_NURSE")) {
                redirectUrl = "/nurse/dashboard";
            }
        
        response.sendRedirect(redirectUrl);
    }
}
