package com.vijay.User_Master.controller;

import com.vijay.User_Master.Helper.ExceptionUtil;
import com.vijay.User_Master.dto.RoleRequest;
import com.vijay.User_Master.dto.RoleUpdateRequest;
import com.vijay.User_Master.dto.UserRoleRequest;
import com.vijay.User_Master.service.RoleManagementService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Role Management Controller
 * Handles all role management operations with synchronous processing
 * to ensure proper JWT token authentication and Spring Security context
 */
@RestController
@RequestMapping("/api/roles")
@AllArgsConstructor
@Log4j2
public class RoleController {

    private final RoleManagementService roleManagementService;

    // ============= BASIC ROLE CRUD OPERATIONS =============

    /**
     * Create a new role.
     * Only accessible by ADMIN users.
     *
     * @param roleRequest The request containing role details.
     * @return ResponseEntity containing the created role.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createRole(@RequestBody RoleRequest roleRequest) {
        log.info("Received request to create role with name: {}", roleRequest.getName());

        try {
            var roleResponse = roleManagementService.createRole(roleRequest);
            log.info("Role created successfully with name: {}", roleResponse.getName());
            return ExceptionUtil.createBuildResponse(roleResponse, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error creating role: {}", e.getMessage());
            return ExceptionUtil.createBuildResponse("Error creating role: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Get Role by ID.
     * Only accessible by ADMIN users.
     *
     * @param id The ID of the role to fetch.
     * @return ResponseEntity containing the role details.
     */


    @GetMapping("/{id:\\d+}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getRoleById(@PathVariable Long id) {
        log.info("Received request to fetch role with ID: {}", id);

        try {
            var roleResponse = roleManagementService.getRoleById(id);
            log.info("Role with ID '{}' fetched successfully", id);
            return ExceptionUtil.createBuildResponse(roleResponse, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error fetching role: {}", e.getMessage());
            return ExceptionUtil.createBuildResponse("Error fetching role: " + e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Get all roles.
     * Only accessible by ADMIN users.
     *
     * @return ResponseEntity containing all roles.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllRoles() {
        log.info("Received request to fetch all roles");

        try {
            var roles = roleManagementService.getAllRoles();
            log.info("Fetched {} roles successfully", roles.size());
            return ExceptionUtil.createBuildResponse(roles, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error fetching roles: {}", e.getMessage());
            return ExceptionUtil.createBuildResponse("Error fetching roles: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update Role.
     * Only accessible by ADMIN users.
     *
     * @param id      The ID of the role to update.
     * @param request The request containing the updated role data.
     * @return ResponseEntity containing the updated role.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateRole(@PathVariable Long id, @RequestBody RoleRequest request) {
        log.info("Received request to update role with ID: {}", id);

        try {
            var roleResponse = roleManagementService.updateRole(id, request);
            log.info("Role with ID '{}' updated successfully", id);
            return ExceptionUtil.createBuildResponse(roleResponse, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error updating role: {}", e.getMessage());
            return ExceptionUtil.createBuildResponse("Error updating role: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Delete Role.
     * Only accessible by ADMIN users.
     *
     * @param id The ID of the role to delete.
     * @return ResponseEntity indicating success.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteRole(@PathVariable Long id) {
        log.info("Received request to delete role with ID: {}", id);

        try {
            boolean success = roleManagementService.deleteRole(id);
            log.info("Role with ID '{}' deleted successfully", id);
            return ExceptionUtil.createBuildResponse("Role deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error deleting role: {}", e.getMessage());
            return ExceptionUtil.createBuildResponse("Error deleting role: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // ============= ROLE MANAGEMENT ENDPOINTS =============

    /**
     * Get all active roles.
     * Only accessible by ADMIN users.
     *
     * @return ResponseEntity containing all active roles.
     */
    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllActiveRoles() {
        log.info("Received request to fetch all active roles");
        
        try {
            var activeRoles = roleManagementService.getAllActiveRoles();
            log.info("Fetched {} active roles successfully", activeRoles.size());
            return ExceptionUtil.createBuildResponse(activeRoles, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error fetching active roles: {}", e.getMessage());
            return ExceptionUtil.createBuildResponse("Error fetching active roles: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update role details.
     * Only accessible by ADMIN users.
     *
     * @param roleId The ID of the role to update.
     * @param updateRequest The request containing updated role details.
     * @return ResponseEntity containing the updated role.
     */
    @PatchMapping("/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateRoleDetails(@PathVariable Long roleId, @RequestBody RoleUpdateRequest updateRequest) {
        log.info("Received request to update role details for role ID: {}", roleId);
        
        try {
            var updatedRole = roleManagementService.updateRoleDetails(roleId, updateRequest);
            log.info("Role with ID '{}' details updated successfully", roleId);
            return ExceptionUtil.createBuildResponse(updatedRole, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error updating role details: {}", e.getMessage());
            return ExceptionUtil.createBuildResponse("Error updating role details: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Activate a role.
     * Only accessible by ADMIN users.
     *
     * @param roleId The ID of the role to activate.
     * @return ResponseEntity indicating success.
     */
    @PatchMapping("/{roleId}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> activateRole(@PathVariable Long roleId) {
        log.info("Received request to activate role with ID: {}", roleId);
        
        try {
            roleManagementService.activateRole(roleId);
            log.info("Role with ID '{}' activated successfully", roleId);
            return ExceptionUtil.createBuildResponse("Role activated successfully", HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error activating role: {}", e.getMessage());
            return ExceptionUtil.createBuildResponse("Error activating role: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Deactivate a role.
     * Only accessible by ADMIN users.
     *
     * @param roleId The ID of the role to deactivate.
     * @return ResponseEntity indicating success.
     */
    @PatchMapping("/{roleId}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deactivateRole(@PathVariable Long roleId) {
        log.info("Received request to deactivate role with ID: {}", roleId);
        
        try {
            roleManagementService.deactivateRole(roleId);
            log.info("Role with ID '{}' deactivated successfully", roleId);
            return ExceptionUtil.createBuildResponse("Role deactivated successfully", HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error deactivating role: {}", e.getMessage());
            return ExceptionUtil.createBuildResponse("Error deactivating role: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // ============= USER ROLE ASSIGNMENT ENDPOINTS =============

    /**
     * Assign roles to a user.
     * Only accessible by ADMIN users.
     *
     * @param userRoleRequest The request containing user ID and role IDs to assign.
     * @return ResponseEntity containing the updated user.
     */
    @PostMapping("/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> assignRolesToUser(@RequestBody UserRoleRequest userRoleRequest) {
        log.info("Received request to assign roles to user ID: {}", userRoleRequest.getUserId());
        
        try {
            var updatedUser = roleManagementService.assignRolesToUser(userRoleRequest);
            log.info("Roles assigned successfully to user ID: {}", userRoleRequest.getUserId());
            return ExceptionUtil.createBuildResponse(updatedUser, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error assigning roles to user: {}", e.getMessage());
            return ExceptionUtil.createBuildResponse("Error assigning roles: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Remove roles from a user.
     * Only accessible by ADMIN users.
     *
     * @param userRoleRequest The request containing user ID and role IDs to remove.
     * @return ResponseEntity containing the updated user.
     */
    @PostMapping("/remove")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> removeRolesFromUser(@RequestBody UserRoleRequest userRoleRequest) {
        log.info("Received request to remove roles from user ID: {}", userRoleRequest.getUserId());
        
        try {
            var updatedUser = roleManagementService.removeRolesFromUser(userRoleRequest);
            log.info("Roles removed successfully from user ID: {}", userRoleRequest.getUserId());
            return ExceptionUtil.createBuildResponse(updatedUser, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error removing roles from user: {}", e.getMessage());
            return ExceptionUtil.createBuildResponse("Error removing roles: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Replace user roles.
     * Only accessible by ADMIN users.
     *
     * @param userRoleRequest The request containing user ID and new role IDs.
     * @return ResponseEntity containing the updated user.
     */
    @PutMapping("/replace")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> replaceUserRoles(@RequestBody UserRoleRequest userRoleRequest) {
        log.info("Received request to replace roles for user ID: {}", userRoleRequest.getUserId());
        
        try {
            var updatedUser = roleManagementService.replaceUserRoles(userRoleRequest);
            log.info("Roles replaced successfully for user ID: {}", userRoleRequest.getUserId());
            return ExceptionUtil.createBuildResponse(updatedUser, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error replacing user roles: {}", e.getMessage());
            return ExceptionUtil.createBuildResponse("Error replacing roles: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Get user roles.
     * Only accessible by ADMIN users.
     *
     * @param userId The ID of the user whose roles to fetch.
     * @return ResponseEntity containing the user's roles.
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserRoles(@PathVariable Long userId) {
        log.info("Received request to fetch roles for user ID: {}", userId);
        
        try {
            var userRoles = roleManagementService.getUserRoles(userId);
            log.info("Fetched {} roles for user ID: {}", userRoles.size(), userId);
            return ExceptionUtil.createBuildResponse(userRoles, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error fetching user roles: {}", e.getMessage());
            return ExceptionUtil.createBuildResponse("Error fetching user roles: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Check if role exists.
     * Only accessible by ADMIN users.
     *
     * @param roleId The ID of the role to check.
     * @return ResponseEntity indicating if the role exists.
     */
    @GetMapping("/{roleId}/exists")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> checkRoleExists(@PathVariable Long roleId) {
        log.info("Received request to check if role exists with ID: {}", roleId);
        
        try {
            boolean exists = roleManagementService.roleExists(roleId);
            log.info("Role existence check for ID '{}': {}", roleId, exists);
            return ExceptionUtil.createBuildResponse(exists, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error checking role existence: {}", e.getMessage());
            return ExceptionUtil.createBuildResponse("Error checking role existence: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Get roles by name.
     * Only accessible by ADMIN users.
     *
     * @param roleName The name of the role to search for.
     * @return ResponseEntity containing matching roles.
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getRolesByName(@RequestParam String roleName) {
        log.info("Received request to search roles by name: {}", roleName);
        
        try {
            var roles = roleManagementService.getRolesByName(roleName);
            log.info("Found {} roles with name: {}", roles.size(), roleName);
            return ExceptionUtil.createBuildResponse(roles, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error searching roles by name: {}", e.getMessage());
            return ExceptionUtil.createBuildResponse("Error searching roles: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
