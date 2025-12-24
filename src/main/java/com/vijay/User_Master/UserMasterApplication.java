package com.vijay.User_Master;

import com.vijay.User_Master.entity.Role;
import com.vijay.User_Master.entity.User;
import com.vijay.User_Master.entity.Worker;
import com.vijay.User_Master.repository.RoleRepository;
import com.vijay.User_Master.repository.UserRepository;
import com.vijay.User_Master.repository.WorkerRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class UserMasterApplication {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WorkerRepository workerRepository;

    @Autowired
    private RoleRepository roleRepository;

    public static void main(String[] args) {
        SpringApplication.run(UserMasterApplication.class, args);
    }

    @PostConstruct
    protected void init() {
// you are running first time application then this method un-comments this.
        // getCurrentAuditor() and comment another
        if (userRepository.count() == 0 && workerRepository.count() == 0) {

            // Ensure roles are created and saved before assigning
            Role adminRole = createAndSaveRole("ROLE_ADMIN");
            Role superUserRole = createAndSaveRole("ROLE_SUPER_USER");
            Role normalRole = createAndSaveRole("ROLE_NORMAL");
            Role workerRole = createAndSaveRole("ROLE_WORKER");

            // Add roles to sets
            Set<Role> adminRoles = new HashSet<>(Arrays.asList(adminRole));
            Set<Role> userRoles = new HashSet<>(Arrays.asList(superUserRole));
            Set<Role> normalRoles = new HashSet<>(Arrays.asList(normalRole));
            Set<Role> workerRoles = new HashSet<>(Arrays.asList(workerRole));

            // Create users
            User admin = User.builder()
                    .name("Vimal Kumar")
                    .email("admin@gmail.com")
                    .username("admin")
                    .password(passwordEncoder.encode("admin"))
                    .roles(adminRoles)
                    .build();

            User user = User.builder()
                    .name("Ajay Rawat")
                    .email("user@gmail.com")
                    .username("user")
                    .password(passwordEncoder.encode("user"))
                    .roles(userRoles)

                    .build();

            User normalUser = User.builder()
                    .name("Vijay Rathod")
                    .email("normal@gmail.com")
                    .username("normal")
                    .password(passwordEncoder.encode("normal"))
                    .roles(normalRoles)
                    .build();

            // Save users
            userRepository.saveAll(Arrays.asList(admin, user, normalUser));

            Worker worker = Worker.builder()
                    .name("Salman Khan")
                    .email("worker@gmail.com")
                    .username("worker")
                    .password(passwordEncoder.encode("worker"))
                    .roles(workerRoles)
                    .user(admin)
                    .build();
            workerRepository.save(worker);
        }

    }

    private Role createAndSaveRole(String roleName) {
        Role role = new Role();
        role.setName(roleName);
        return roleRepository.save(role);
    }
}
