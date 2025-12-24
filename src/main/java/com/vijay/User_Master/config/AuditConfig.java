package com.vijay.User_Master.config;



import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditAware")
public class AuditConfig {
    // This class can be left empty, it's just to enable JPA Auditing
}
