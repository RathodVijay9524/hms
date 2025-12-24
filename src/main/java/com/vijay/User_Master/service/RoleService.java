package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.RoleRequest;
import com.vijay.User_Master.dto.RoleResponse;
import com.vijay.User_Master.dto.RoleUpdateRequest;
import com.vijay.User_Master.dto.UserRoleRequest;
import com.vijay.User_Master.dto.UserResponse;
import com.vijay.User_Master.service.generics.iCrudService;

import java.util.List;
import java.util.Set;

public interface RoleService extends iCrudService<RoleRequest, RoleResponse,Long> {
    
    // Role management methods
    List<RoleResponse> getAllActiveRoles();
    RoleResponse updateRole(Long roleId, RoleUpdateRequest updateRequest);
    void activateRole(Long roleId);
    void deactivateRole(Long roleId);
    
    // User role assignment methods
    UserResponse assignRolesToUser(UserRoleRequest userRoleRequest);
    UserResponse removeRolesFromUser(UserRoleRequest userRoleRequest);
    UserResponse replaceUserRoles(UserRoleRequest userRoleRequest);
    Set<RoleResponse> getUserRoles(Long userId);
    
    // Role validation
    boolean roleExists(Long roleId);
    boolean roleExistsByName(String roleName);
}
