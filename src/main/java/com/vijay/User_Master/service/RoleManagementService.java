package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.RoleRequest;
import com.vijay.User_Master.dto.RoleResponse;
import com.vijay.User_Master.dto.RoleUpdateRequest;
import com.vijay.User_Master.dto.UserRoleRequest;
import com.vijay.User_Master.dto.UserResponse;

import java.util.List;
import java.util.Set;

/**
 * Synchronous Role Management Service Interface
 * This service handles all role management operations without CompletableFuture
 * to ensure proper JWT token authentication and Spring Security context propagation
 */
public interface RoleManagementService {
    
    // Basic role CRUD operations (synchronous)
    RoleResponse createRole(RoleRequest roleRequest);
    RoleResponse getRoleById(Long roleId);
    List<RoleResponse> getAllRoles();
    RoleResponse updateRole(Long roleId, RoleRequest roleRequest);
    boolean deleteRole(Long roleId);
    
    // Role management methods
    List<RoleResponse> getAllActiveRoles();
    RoleResponse updateRoleDetails(Long roleId, RoleUpdateRequest updateRequest);
    void activateRole(Long roleId);
    void deactivateRole(Long roleId);
    
    // User role assignment methods
    UserResponse assignRolesToUser(UserRoleRequest userRoleRequest);
    UserResponse removeRolesFromUser(UserRoleRequest userRoleRequest);
    UserResponse replaceUserRoles(UserRoleRequest userRoleRequest);
    Set<RoleResponse> getUserRoles(Long userId);
    
    // Role validation methods
    boolean roleExists(Long roleId);
    boolean roleExistsByName(String roleName);
    
    // Role status management
    List<RoleResponse> getActiveRolesByIds(Set<Long> roleIds);
    List<RoleResponse> getRolesByName(String roleName);
}
