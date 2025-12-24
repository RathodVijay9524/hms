package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.Helper.Helper;
import com.vijay.User_Master.dto.PageableResponse;
import com.vijay.User_Master.dto.UserRequest;
import com.vijay.User_Master.dto.UserResponse;
import com.vijay.User_Master.entity.AccountStatus;
import com.vijay.User_Master.entity.Role;
import com.vijay.User_Master.entity.User;
import com.vijay.User_Master.exceptions.BadApiRequestException;
import com.vijay.User_Master.exceptions.ResourceNotFoundException;
import com.vijay.User_Master.exceptions.UserAlreadyExistsException;
import com.vijay.User_Master.repository.AccountStatusRepository;
import com.vijay.User_Master.repository.RoleRepository;
import com.vijay.User_Master.repository.UserRepository;
import com.vijay.User_Master.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Log4j2
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final AccountStatusRepository accountStatusRepository;
    private final ModelMapper mapper;


    @Transactional
    public void updateAccountStatus(Long userId, Boolean isActive) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        AccountStatus accountStatus = user.getAccountStatus();
        if (accountStatus == null) {
            accountStatus = new AccountStatus(); // New status
        }

        accountStatus.setIsActive(isActive); // update the status
        user.setAccountStatus(accountStatus); // assign to user

        userRepository.save(user); // cascade should handle persist/update
    }



    @Override
    public PageableResponse<UserResponse> getAllActiveUsers(int pageNumber, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<User> usersPage = userRepository.findAllByIsDeletedFalse(pageable);
        return Helper.getPageableResponse(usersPage, UserResponse.class);
    }

    @Override
    public PageableResponse<UserResponse> getAllDeletedUsers(int pageNumber, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<User> usersPage = userRepository.findAllByIsDeletedTrue(pageable);
        return Helper.getPageableResponse(usersPage, UserResponse.class);
    }

    @Override
    public PageableResponse<UserResponse> getUsersWithFilters(
            int pageNumber, int pageSize, String sortBy, String sortDir,
            Boolean isDeleted, Boolean isActive) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Specification<User> spec = Specification.where(null);

        if (isDeleted != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("isDeleted"), isDeleted));
        }

        if (isActive != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.join("accountStatus").get("isActive"), isActive));
        }

        Page<User> usersPage = userRepository.findAll(spec, pageable);
        return Helper.getPageableResponse(usersPage, UserResponse.class);
    }

    @Override
    public Page<UserResponse> getUsersWithFilter(Boolean isDeleted, Boolean isActive,String keyword, Pageable pageable) {
        Page<User> users;
        if (StringUtils.hasText(keyword)) {
            users = userRepository.searchUsersWithKeyword(keyword, isDeleted, isActive, pageable);
        } else {
            if (isDeleted != null && isActive != null) {
                users = userRepository.findAllByIsDeletedAndAccountStatus_IsActive(isDeleted, isActive, pageable);
            } else if (isDeleted != null) {
                users = userRepository.findAllByIsDeleted(isDeleted, pageable);
            } else if (isActive != null) {
                users = userRepository.findAllByAccountStatus_IsActive(isActive, pageable);
            } else {
                users = userRepository.findAll(pageable);
            }
        }

        return users.map(user -> mapper.map(user, UserResponse.class));
    }


    @Override
    public void softDeleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setDeleted(true);
        userRepository.save(user);
    }


    @Override
    public void restoreUser(Long id) {
        User user = getUserOrThrow(id);
        user.setDeleted(false);
        userRepository.save(user);
    }

    @Override
    public void permanentlyDelete(Long id) {
        User user = getUserOrThrow(id);
        userRepository.delete(user);
    }
    private User getUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
    }

    @Override
    public UserResponse updateUser(Long id, UserRequest request) {
        User user=userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("USER", "ID", id));

        // Update username
        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new UserAlreadyExistsException("Username is already taken");
            }
            user.setUsername(request.getUsername());
            log.info("This image name from user-service: {}", request.getImageName());
        }

        // Set image name
        if (request.getImageName() != null) {
            user.setImageName(request.getImageName());
            log.info("Image name set from request: {}", request.getImageName());
        }

        // Update email
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new UserAlreadyExistsException("Email is already in use");
            }
            user.setEmail(request.getEmail());
        }

        // Update roles
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            Set<Role> roles = request.getRoles().stream()
                    .map(roleName -> roleRepository.findByName(String.valueOf(roleName))
                            .orElseThrow(() -> new RuntimeException("Role not found with name: " + roleName)))
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        }

        // Update password
        if (request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        // Save and return updated user
        userRepository.save(user);
        return mapper.map(user, UserResponse.class);

    }

    @Override
    public UserResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if authentication is null or if the user is anonymous
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new IllegalStateException("User is not authenticated.");
        }
        String username = authentication.getName();
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Check if userDetails is null (user not found)
        if (userDetails == null) {
            throw new IllegalStateException("User details not found.");
        }
        return mapper.map(userDetails, UserResponse.class);
    }

    @Override
    public UserResponse getByIdForUser(Long aLong) {
        User user = userRepository.findById(aLong)
                .orElseThrow(() -> {
                    log.error("User with ID '{}' not found", aLong);
                    return new ResourceNotFoundException("USER", "ID", aLong);
                });

        return mapper.map(user, UserResponse.class);
    }

    @Override
    public CompletableFuture<UserResponse> create(UserRequest request) {
        // Start logging
        log.info("Attempting to create a new user with username: {}", request.getUsername());

        return CompletableFuture.supplyAsync(() -> {
            // Check if the username already exists in the database
            if (userRepository.existsByUsername(request.getUsername())) {
                log.error("Username '{}' already exists", request.getUsername());
                throw new UserAlreadyExistsException("Username is already taken");
            }

            // Check if the email already exists
            if (userRepository.existsByEmail(request.getEmail())) {
                log.error("Email '{}' already exists", request.getEmail());
                throw new UserAlreadyExistsException("Email is already in use");
            }

            // Map the request to a User entity
            User user = mapper.map(request, User.class);
            // Encode password before saving
            user.setPassword(passwordEncoder.encode(request.getPassword()));


            // Assign the default role (USER)
            Role role = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> {
                        log.error("Role 'USER' not found");
                        return new BadApiRequestException("Role Not found with Name");
                    });
            user.setRoles(Set.of(role));

            // Save the user in the repository
            userRepository.save(user);

            // Log user creation
            log.info("User with username '{}' created successfully", user.getUsername());

            // Map the saved user to UserResponse and return
            return mapper.map(user, UserResponse.class);

        });
    }

    @Override
    public CompletableFuture<UserResponse> getById(Long aLong) {
        return null;
    }




    @Override
    public CompletableFuture<Set<UserResponse>> getAll() {
        return CompletableFuture.supplyAsync(() -> {
            List<User> users = userRepository.findAll();
            return users.stream()
                    .map(user -> mapper.map(user, UserResponse.class))
                    .collect(Collectors.toSet());
        });
    }

    @Override
    public CompletableFuture<UserResponse> update(Long aLong, UserRequest request) {
        return null;
    }

    @Override
    public CompletableFuture<Boolean> delete(Long aLong) {
        return CompletableFuture.supplyAsync(() -> {
            if(!userRepository.existsById(aLong)) {
                throw new ResourceNotFoundException("USER", "ID", aLong);
            }
            userRepository.deleteById(aLong);
            return true;
        });
    }
}
