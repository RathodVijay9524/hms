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
import com.vijay.User_Master.service.RoleManagementService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Synchronous Role Management Service Implementation
 * This service handles all role management operations without CompletableFuture
 * to ensure proper JWT token authentication and Spring Security context propagation
 */
@Service
@AllArgsConstructor
@Log4j2
public class RoleManagementServiceImpl implements RoleManagementService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final ModelMapper mapper;

    // ============= BASIC ROLE CRUD OPERATIONS =============

    @Override
    @Transactional
    public RoleResponse createRole(RoleRequest roleRequest) {
        log.info("Creating new role with name: {}", roleRequest.getName());
        
        // Check if role already exists
        if (roleExistsByName(roleRequest.getName())) {
            throw new IllegalArgumentException("Role with name '" + roleRequest.getName() + "' already exists");
        }
        
        Role role = mapper.map(roleRequest, Role.class);
        role.setActive(true);
        role.setDeleted(false);
        
        Role savedRole = roleRepository.save(role);
        log.info("Role created successfully with ID: {} and name: {}", savedRole.getId(), savedRole.getName());
        
        return mapper.map(savedRole, RoleResponse.class);
    }

    @Override
    public RoleResponse getRoleById(Long roleId) {
        log.info("Fetching role with ID: {}", roleId);
        
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> {
                    log.error("Role with ID '{}' not found", roleId);
                    return new ResourceNotFoundException("Role", "id", roleId);
                });
        
        log.info("Role with ID '{}' found: {}", roleId, role.getName());
        return mapper.map(role, RoleResponse.class);
    }

    @Override
    public List<RoleResponse> getAllRoles() {
        log.info("Fetching all roles");
        
        List<Role> roles = roleRepository.findAll();
        List<RoleResponse> roleResponses = roles.stream()
                .map(role -> mapper.map(role, RoleResponse.class))
                .collect(Collectors.toList());
        
        log.info("Fetched {} roles successfully", roleResponses.size());
        return roleResponses;
    }

    @Override
    @Transactional
    public RoleResponse updateRole(Long roleId, RoleRequest roleRequest) {
        log.info("Updating role with ID: {}", roleId);
        
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> {
                    log.error("Role with ID '{}' not found", roleId);
                    return new ResourceNotFoundException("Role", "id", roleId);
                });
        
        // Update fields only if provided in the request
        if (roleRequest.getName() != null && !roleRequest.getName().trim().isEmpty()) {
            // Check if new name already exists for another role
            roleRepository.findByName(roleRequest.getName())
                    .ifPresent(existingRole -> {
                        if (!existingRole.getId().equals(roleId)) {
                            throw new IllegalArgumentException("Role with name '" + roleRequest.getName() + "' already exists");
                        }
                    });
            role.setName(roleRequest.getName());
        }
        
        Role savedRole = roleRepository.save(role);
        log.info("Role with ID '{}' updated successfully", roleId);
        
        return mapper.map(savedRole, RoleResponse.class);
    }

    @Override
    @Transactional
    public boolean deleteRole(Long roleId) {
        log.info("Deleting role with ID: {}", roleId);
        
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> {
                    log.error("Role with ID '{}' not found", roleId);
                    return new ResourceNotFoundException("Role", "id", roleId);
                });
        
        // Soft delete
        role.setDeleted(true);
        role.setActive(false);
        roleRepository.save(role);
        
        log.info("Role with ID '{}' deleted successfully", roleId);
        return true;
    }

    // ============= ROLE MANAGEMENT METHODS =============

    @Override
    public List<RoleResponse> getAllActiveRoles() {
        log.info("Fetching all active roles");
        
        List<Role> activeRoles = roleRepository.findAll().stream()
                .filter(role -> role.isActive() && !role.isDeleted())
                .collect(Collectors.toList());
        
        List<RoleResponse> roleResponses = activeRoles.stream()
                .map(role -> mapper.map(role, RoleResponse.class))
                .collect(Collectors.toList());
        
        log.info("Fetched {} active roles", roleResponses.size());
        return roleResponses;
    }

    @Override
    @Transactional
    public RoleResponse updateRoleDetails(Long roleId, RoleUpdateRequest updateRequest) {
        log.info("Updating role details for role ID: {}", roleId);
        
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", roleId));
        
        if (updateRequest.getName() != null && !updateRequest.getName().trim().isEmpty()) {
            // Check if new name already exists for another role
            roleRepository.findByName(updateRequest.getName())
                    .ifPresent(existingRole -> {
                        if (!existingRole.getId().equals(roleId)) {
                            throw new IllegalArgumentException("Role with name '" + updateRequest.getName() + "' already exists");
                        }
                    });
            role.setName(updateRequest.getName());
        }
        
        if (updateRequest.getIsActive() != null) {
            role.setActive(updateRequest.getIsActive());
        }
        
        Role savedRole = roleRepository.save(role);
        log.info("Role details updated successfully for role ID: {}", roleId);
        
        return mapper.map(savedRole, RoleResponse.class);
    }

    @Override
    @Transactional
    public void activateRole(Long roleId) {
        log.info("Activating role with ID: {}", roleId);
        
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", roleId));
        
        role.setActive(true);
        roleRepository.save(role);
        
        log.info("Role with ID: {} activated successfully", roleId);
    }

    @Override
    @Transactional
    public void deactivateRole(Long roleId) {
        log.info("Deactivating role with ID: {}", roleId);
        
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", roleId));
        
        role.setActive(false);
        roleRepository.save(role);
        
        log.info("Role with ID: {} deactivated successfully", roleId);
    }

    // ============= USER ROLE ASSIGNMENT METHODS =============

    @Override
    @Transactional
    public UserResponse assignRolesToUser(UserRoleRequest userRoleRequest) {
        log.info("Assigning roles to user with ID: {}", userRoleRequest.getUserId());
        
        User user = userRepository.findById(userRoleRequest.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userRoleRequest.getUserId()));
        
        Set<Role> rolesToAssign = userRoleRequest.getRoleIds().stream()
                .map(roleId -> roleRepository.findById(roleId)
                        .orElseThrow(() -> new ResourceNotFoundException("Role", "id", roleId)))
                .filter(role -> role.isActive() && !role.isDeleted()) // Only assign active roles
                .collect(Collectors.toSet());
        
        if (rolesToAssign.isEmpty()) {
            throw new IllegalArgumentException("No valid active roles found to assign");
        }
        
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
    @Transactional
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
    @Transactional
    public UserResponse replaceUserRoles(UserRoleRequest userRoleRequest) {
        log.info("Replacing roles for user with ID: {}", userRoleRequest.getUserId());
        
        User user = userRepository.findById(userRoleRequest.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userRoleRequest.getUserId()));
        
        Set<Role> newRoles = userRoleRequest.getRoleIds().stream()
                .map(roleId -> roleRepository.findById(roleId)
                        .orElseThrow(() -> new ResourceNotFoundException("Role", "id", roleId)))
                .filter(role -> role.isActive() && !role.isDeleted()) // Only assign active roles
                .collect(Collectors.toSet());
        
        if (newRoles.isEmpty()) {
            throw new IllegalArgumentException("No valid active roles found to assign");
        }
        
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
        
        Set<RoleResponse> userRoles = user.getRoles().stream()
                .map(role -> mapper.map(role, RoleResponse.class))
                .collect(Collectors.toSet());
        
        log.info("Fetched {} roles for user with ID: {}", userRoles.size(), userId);
        return userRoles;
    }

    // ============= ROLE VALIDATION METHODS =============

    @Override
    public boolean roleExists(Long roleId) {
        boolean exists = roleRepository.existsById(roleId);
        log.debug("Role existence check for ID '{}': {}", roleId, exists);
        return exists;
    }

    @Override
    public boolean roleExistsByName(String roleName) {
        boolean exists = roleRepository.findByName(roleName).isPresent();
        log.debug("Role existence check for name '{}': {}", roleName, exists);
        return exists;
    }

    @Override
    public List<RoleResponse> getActiveRolesByIds(Set<Long> roleIds) {
        log.info("Fetching active roles by IDs: {}", roleIds);
        
        List<Role> roles = roleRepository.findAllById(roleIds).stream()
                .filter(role -> role.isActive() && !role.isDeleted())
                .collect(Collectors.toList());
        
        return roles.stream()
                .map(role -> mapper.map(role, RoleResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<RoleResponse> getRolesByName(String roleName) {
        log.info("Fetching roles by name: {}", roleName);
        
        return roleRepository.findByName(roleName)
                .map(role -> mapper.map(role, RoleResponse.class))
                .map(List::of)
                .orElse(List.of());
    }
}
