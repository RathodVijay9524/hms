package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.dto.RoleRequest;
import com.vijay.User_Master.dto.RoleResponse;
import com.vijay.User_Master.dto.RoleUpdateRequest;
import com.vijay.User_Master.dto.UserRoleRequest;
import com.vijay.User_Master.dto.UserResponse;
import com.vijay.User_Master.entity.Role;
import com.vijay.User_Master.entity.User;
import com.vijay.User_Master.exceptions.ResourceNotFoundException;
import com.vijay.User_Master.repository.RoleRepository;
import com.vijay.User_Master.repository.UserRepository;
import com.vijay.User_Master.service.RoleService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Log4j2
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final ModelMapper mapper;

    // Create Role For Users
    @Override
    public CompletableFuture<RoleResponse> create(RoleRequest request) {
        return CompletableFuture.supplyAsync(()->{
            Role role = mapper.map(request, Role.class);
            role.setActive(true);
            roleRepository.save(role);
            return mapper.map(role,RoleResponse.class);
        });
    }

    // Get Role by ID
    @Override
    public CompletableFuture<RoleResponse> getById(Long id) {
        return CompletableFuture.supplyAsync(() -> {
            // Log the fetch attempt
            log.info("Attempting to fetch role with ID: {}", id);

            // Fetch the role by ID and handle possible not found exception
            Role role = roleRepository.findById(id)
                    .orElseThrow(() -> {
                        log.error("Role with ID '{}' not found", id);
                        return new ResourceNotFoundException("Role", "id", id);
                    });

            // Log success
            log.info("Role with ID '{}' found", id);

            // Map and return the Role as RoleResponse
            return mapper.map(role, RoleResponse.class);
        });
    }

    // Get all roles
    @Override
    public CompletableFuture<Set<RoleResponse>> getAll() {
        return CompletableFuture.supplyAsync(() -> {
            // Log the attempt to fetch all roles
            log.info("Attempting to fetch all roles.");

            // Fetch all roles and map them to RoleResponse
            Set<Role> roles = new HashSet<>(roleRepository.findAll());
            Set<RoleResponse> roleResponses = roles.stream()
                    .map(role -> mapper.map(role, RoleResponse.class))
                    .collect(Collectors.toSet());

            // Log success
            log.info("Fetched {} roles successfully.", roleResponses.size());

            return roleResponses;
        });
    }

    // Update an existing role
    @Override
    public CompletableFuture<RoleResponse> update(Long id, RoleRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            // Log the update attempt
            log.info("Attempting to update role with ID: {}", id);

            // Fetch the existing role by ID
            Role role = roleRepository.findById(id)
                    .orElseThrow(() -> {
                        log.error("Role with ID '{}' not found", id);
                        return new ResourceNotFoundException("Role", "id", id);
                    });

            // Update fields only if provided in the request
            if (request.getName() != null) {
                role.setName(request.getName());
            }
            // Save the updated role
            roleRepository.save(role);

            // Log success
            log.info("Role with ID '{}' updated successfully", id);

            // Map and return the updated Role as RoleResponse
            return mapper.map(role, RoleResponse.class);
        });
    }

    // Delete a role by ID
    @Override
    public CompletableFuture<Boolean> delete(Long id) {
        return CompletableFuture.supplyAsync(() -> {
            // Log the delete attempt
            log.info("Attempting to delete role with ID: {}", id);

            // Fetch the role to be deleted
            Role role = roleRepository.findById(id)
                    .orElseThrow(() -> {
                        log.error("Role with ID '{}' not found", id);
                        return new ResourceNotFoundException("Role", "id", id);
                    });

            // Delete the role
            roleRepository.delete(role);

            // Log success
            log.info("Role with ID '{}' deleted successfully", id);

            return true;
        });
    }

    // ============= NEW ROLE MANAGEMENT METHODS =============

    @Override
    public List<RoleResponse> getAllActiveRoles() {
        log.info("Fetching all active roles");
        List<Role> activeRoles = roleRepository.findAll().stream()
                .filter(role -> role.isActive() && !role.isDeleted())
                .collect(Collectors.toList());
        
        return activeRoles.stream()
                .map(role -> mapper.map(role, RoleResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public RoleResponse updateRole(Long roleId, RoleUpdateRequest updateRequest) {
        log.info("Updating role with ID: {}", roleId);
        
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", roleId));
        
        if (updateRequest.getName() != null) {
            role.setName(updateRequest.getName());
        }
        if (updateRequest.getIsActive() != null) {
            role.setActive(updateRequest.getIsActive());
        }
        
        Role savedRole = roleRepository.save(role);
        log.info("Role with ID: {} updated successfully", roleId);
        
        return mapper.map(savedRole, RoleResponse.class);
    }

    @Override
    public void activateRole(Long roleId) {
        log.info("Activating role with ID: {}", roleId);
        
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", roleId));
        
        role.setActive(true);
        roleRepository.save(role);
        
        log.info("Role with ID: {} activated successfully", roleId);
    }

    @Override
    public void deactivateRole(Long roleId) {
        log.info("Deactivating role with ID: {}", roleId);
        
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", roleId));
        
        role.setActive(false);
        roleRepository.save(role);
        
        log.info("Role with ID: {} deactivated successfully", roleId);
    }

    @Override
    public UserResponse assignRolesToUser(UserRoleRequest userRoleRequest) {
        log.info("Assigning roles to user with ID: {}", userRoleRequest.getUserId());
        
        User user = userRepository.findById(userRoleRequest.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userRoleRequest.getUserId()));
        
        Set<Role> rolesToAssign = userRoleRequest.getRoleIds().stream()
                .map(roleId -> roleRepository.findById(roleId)
                        .orElseThrow(() -> new ResourceNotFoundException("Role", "id", roleId)))
                .collect(Collectors.toSet());
        
        // Add new roles to existing roles
        if (user.getRoles() == null) {
            user.setRoles(new HashSet<>());
        }
        user.getRoles().addAll(rolesToAssign);
        
        User savedUser = userRepository.save(user);
        log.info("Roles assigned successfully to user with ID: {}", userRoleRequest.getUserId());
        
        return mapper.map(savedUser, UserResponse.class);
    }

    @Override
    public UserResponse removeRolesFromUser(UserRoleRequest userRoleRequest) {
        log.info("Removing roles from user with ID: {}", userRoleRequest.getUserId());
        
        User user = userRepository.findById(userRoleRequest.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userRoleRequest.getUserId()));
        
        Set<Role> rolesToRemove = userRoleRequest.getRoleIds().stream()
                .map(roleId -> roleRepository.findById(roleId)
                        .orElseThrow(() -> new ResourceNotFoundException("Role", "id", roleId)))
                .collect(Collectors.toSet());
        
        // Remove specified roles
        if (user.getRoles() != null) {
            user.getRoles().removeAll(rolesToRemove);
        }
        
        User savedUser = userRepository.save(user);
        log.info("Roles removed successfully from user with ID: {}", userRoleRequest.getUserId());
        
        return mapper.map(savedUser, UserResponse.class);
    }

    @Override
    public UserResponse replaceUserRoles(UserRoleRequest userRoleRequest) {
        log.info("Replacing roles for user with ID: {}", userRoleRequest.getUserId());
        
        User user = userRepository.findById(userRoleRequest.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userRoleRequest.getUserId()));
        
        Set<Role> newRoles = userRoleRequest.getRoleIds().stream()
                .map(roleId -> roleRepository.findById(roleId)
                        .orElseThrow(() -> new ResourceNotFoundException("Role", "id", roleId)))
                .collect(Collectors.toSet());
        
        // Replace all roles with new ones
        user.setRoles(newRoles);
        
        User savedUser = userRepository.save(user);
        log.info("Roles replaced successfully for user with ID: {}", userRoleRequest.getUserId());
        
        return mapper.map(savedUser, UserResponse.class);
    }

    @Override
    public Set<RoleResponse> getUserRoles(Long userId) {
        log.info("Fetching roles for user with ID: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        if (user.getRoles() == null) {
            return new HashSet<>();
        }
        
        return user.getRoles().stream()
                .map(role -> mapper.map(role, RoleResponse.class))
                .collect(Collectors.toSet());
    }

    @Override
    public boolean roleExists(Long roleId) {
        return roleRepository.existsById(roleId);
    }

    @Override
    public boolean roleExistsByName(String roleName) {
        return roleRepository.findByName(roleName).isPresent();
    }
}
