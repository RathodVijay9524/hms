package com.vijay.User_Master.config;

import com.vijay.User_Master.entity.Role;
import com.vijay.User_Master.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Log4j2
public class RoleDataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        List<String> roles = Arrays.asList(
            "ROLE_ADMIN", "ROLE_SUPER_ADMIN", "ROLE_IT_SUPPORT",
            "ROLE_DOCTOR", "ROLE_NURSE", "ROLE_SURGEON", "ROLE_ANESTHESIOLOGIST", "ROLE_PHYSIOTHERAPIST",
            "ROLE_PATIENT", "ROLE_RECEPTIONIST", "ROLE_FRONT_DESK",
            "ROLE_LAB_TECHNICIAN", "ROLE_RADIOLOGIST", "ROLE_PHARMACIST",
            "ROLE_BILLING", "ROLE_ACCOUNTANT", "ROLE_INSURANCE_COORDINATOR",
            "ROLE_HOSPITAL_MANAGER", "ROLE_DEPARTMENT_HEAD", "ROLE_HR", "ROLE_INVENTORY_MANAGER", "ROLE_PROCUREMENT",
            "ROLE_MEDICAL_RECORDS", "ROLE_COMPLIANCE_OFFICER", "ROLE_SECURITY"
        );

        log.info("Starting role initialization...");
        for (String roleName : roles) {
            if (roleRepository.findByName(roleName).isEmpty()) {
                Role role = new Role();
                role.setName(roleName);
                role.setActive(true);
                role.setDeleted(false);
                roleRepository.save(role);
                log.info("Initialized role: {}", roleName);
            }
        }
        log.info("Role initialization completed.");
    }
}
