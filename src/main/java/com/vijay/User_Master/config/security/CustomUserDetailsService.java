package com.vijay.User_Master.config.security;

import com.vijay.User_Master.entity.User;
import com.vijay.User_Master.entity.Worker;
import com.vijay.User_Master.repository.UserRepository;
import com.vijay.User_Master.repository.WorkerRepository;
import lombok.AllArgsConstructor;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Optional;

@AllArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private WorkerRepository workerRepository;
    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return CustomUserDetails.build(user);
        } else {
            Optional<Worker> workerOptional = workerRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
            Worker worker = workerOptional.orElseThrow(() -> new UsernameNotFoundException("Worker not found with username: " + usernameOrEmail));
            return CustomUserDetails.build(worker);
        }
    }
    public UserDetails loadUserByUsernameOrEmail(String username, String email) {
        Optional<User> userOptional = userRepository.findByUsernameOrEmail(username, email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return CustomUserDetails.build(user);
        } else {
            Optional<Worker> workerOptional = workerRepository.findByUsernameOrEmail(username, email);
            Worker worker = workerOptional.orElseThrow(() -> new UsernameNotFoundException("Worker not found with username: " + username + " or email: " + email));
            return CustomUserDetails.build(worker);
        }
    }
}
