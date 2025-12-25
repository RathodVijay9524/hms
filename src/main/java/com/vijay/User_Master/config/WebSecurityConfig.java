package com.vijay.User_Master.config;

import com.vijay.User_Master.config.security.JwtAuthenticationEntryPoint;
import com.vijay.User_Master.config.security.JwtAuthenticationFilter;
import lombok.AllArgsConstructor;
import org.apache.catalina.filters.CorsFilter;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class WebSecurityConfig {

    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final com.vijay.User_Master.config.security.CustomAuthenticationSuccessHandler successHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        // 0) CORS preflight must be open
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 1) All common static resources (css/js/images/webjars/favicon, etc.)
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()

                        // 2) Your explicit static & root fallbacks (Vite assets etc.)
                        .requestMatchers(
                                "/", "/index.html",
                                "/css/**", "/js/**",
                                "/assets/**", "/static/**",
                                "/favicon.ico", "/vite.svg",
                                "/manifest.*", "/robots.txt",
                                "/error"
                        ).permitAll()

                        // 3) Public endpoints
                        .requestMatchers(
                                "/api/auth/login",
                                "/login", "/signin", "/register", "/forgot-password", "/reset-password", "/verify-account", "/select-role",
                                "/api/auth/register/**",
                                "/api/v1/home/**",
                                "/api/v1/tokens/**",
                                "/api/users/image/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/tutorials/**" // Assuming tutorials is public or has its own logic
                        ).permitAll()
                        
                        // Portal Specific Restrictions
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/owner/**").hasAnyRole("OWNER", "ADMIN")
                        .requestMatchers("/doctor/**").hasAnyRole("DOCTOR", "ADMIN")
                        .requestMatchers("/reception/**").hasAnyRole("RECEPTIONIST", "ADMIN")
                        .requestMatchers("/lab/**").hasAnyRole("LAB_TECHNICIAN", "DOCTOR", "ADMIN")
                        .requestMatchers("/billing/**").hasAnyRole("BILLING", "ACCOUNTANT", "ADMIN")
                        .requestMatchers("/patient/**").hasAnyRole("PATIENT", "ADMIN")
                        .requestMatchers("/pharmacy/**").hasAnyRole("PHARMACIST", "ADMIN")
                        .requestMatchers("/nurse/**").hasAnyRole("NURSE", "ADMIN")
                        .requestMatchers("/insurance/**").hasAnyRole("INSURANCE", "ADMIN")
                        .requestMatchers("/analytics/**").hasAnyRole("ANALYST", "ADMIN")
                        
                        // Require authentication for all other endpoints
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("usernameOrEmail")
                        .passwordParameter("password")
                        .successHandler(successHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .exceptionHandling(ex -> ex.authenticationEntryPoint(authenticationEntryPoint))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
