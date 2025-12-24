# Role Management System Guide

## 📋 Overview

This guide provides comprehensive documentation for the Role Management System implemented in the User Master Application. The system allows administrators to manage user roles, assign/remove roles from users, and control role permissions with fine-grained access control.

## 🔐 Security & Access Control

All role management endpoints are protected with `@PreAuthorize("hasRole('ADMIN')")`, ensuring only users with ADMIN role can perform role management operations.

## 🏗️ Architecture Components

### DTOs (Data Transfer Objects)

#### 1. UserRoleRequest
```java
{
    "userId": 1,
    "roleIds": [1, 2, 3],
    "action": "ASSIGN" // ASSIGN, REMOVE, REPLACE
}
```

#### 2. RoleUpdateRequest
```java
{
    "name": "ROLE_MANAGER",
    "isActive": true
}
```

#### 3. RoleResponse
```java
{
    "id": 1,
    "name": "ROLE_ADMIN",
    "isActive": true,
    "isDeleted": false
}
```

## 🌐 API Endpoints

### Role Management Endpoints

#### 1. Get All Active Roles
```http
GET /api/roles/active
Authorization: Bearer {jwt_token}
```

**Response:**
```json
{
    "responseStatus": "OK",
    "status": "success",
    "message": "success",
    "data": [
        {
            "id": 1,
            "name": "ROLE_ADMIN",
            "isActive": true,
            "isDeleted": false
        },
        {
            "id": 2,
            "name": "ROLE_USER",
            "isActive": true,
            "isDeleted": false
        }
    ]
}
```

#### 2. Update Role Details
```http
PATCH /api/roles/{roleId}
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
    "name": "ROLE_MANAGER",
    "isActive": true
}
```

#### 3. Activate Role
```http
PATCH /api/roles/{roleId}/activate
Authorization: Bearer {jwt_token}
```

#### 4. Deactivate Role
```http
PATCH /api/roles/{roleId}/deactivate
Authorization: Bearer {jwt_token}
```

#### 5. Check Role Existence
```http
GET /api/roles/{roleId}/exists
Authorization: Bearer {jwt_token}
```

### User Role Assignment Endpoints

#### 1. Assign Roles to User
```http
POST /api/roles/assign
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
    "userId": 1,
    "roleIds": [1, 2]
}
```

**Description:** Adds new roles to a user while keeping existing roles.

#### 2. Remove Roles from User
```http
POST /api/roles/remove
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
    "userId": 1,
    "roleIds": [2]
}
```

**Description:** Removes specified roles from a user.

#### 3. Replace User Roles
```http
PUT /api/roles/replace
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
    "userId": 1,
    "roleIds": [1, 3]
}
```

**Description:** Replaces all existing roles of a user with new ones.

#### 4. Get User Roles
```http
GET /api/roles/user/{userId}
Authorization: Bearer {jwt_token}
```

**Response:**
```json
{
    "responseStatus": "OK",
    "status": "success",
    "message": "success",
    "data": [
        {
            "id": 1,
            "name": "ROLE_ADMIN",
            "isActive": true,
            "isDeleted": false
        }
    ]
}
```

## 🔧 Usage Examples

### Example 1: Admin Assigns Manager Role to User

```bash
# Step 1: Login as Admin
curl -X POST http://localhost:9091/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'

# Step 2: Get JWT token from response and assign role
curl -X POST http://localhost:9091/api/roles/assign \
  -H "Authorization: Bearer {jwt_token}" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 5,
    "roleIds": [2, 3]
  }'
```

### Example 2: Update Role Status

```bash
# Activate a role
curl -X PATCH http://localhost:9091/api/roles/2/activate \
  -H "Authorization: Bearer {jwt_token}"

# Deactivate a role
curl -X PATCH http://localhost:9091/api/roles/2/deactivate \
  -H "Authorization: Bearer {jwt_token}"
```

### Example 3: Replace User Roles

```bash
# Replace all roles for user ID 5 with only ADMIN role
curl -X PUT http://localhost:9091/api/roles/replace \
  -H "Authorization: Bearer {jwt_token}" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 5,
    "roleIds": [1]
  }'
```

## 🏆 Advanced Features

### 1. Role Validation
- Automatic validation of role existence before assignment
- Prevention of duplicate role assignments
- Validation of user existence before role operations

### 2. Audit Logging
- Comprehensive logging of all role management operations
- Success and error logging for troubleshooting
- User action tracking for security audits

### 3. Flexible Role Operations
- **ASSIGN**: Add new roles while keeping existing ones
- **REMOVE**: Remove specific roles from user
- **REPLACE**: Replace all existing roles with new ones

### 4. Role Status Management
- Activate/Deactivate roles without deletion
- Maintain role history and audit trails
- Soft delete functionality for roles

## 🔍 Business Logic

### Role Assignment Logic
1. **Assign Roles**: Adds new roles to user's existing role set
2. **Remove Roles**: Removes specified roles from user's role set
3. **Replace Roles**: Clears all existing roles and assigns new ones

### Role Validation
- Validates role existence before assignment
- Checks user existence before role operations
- Prevents assignment of inactive or deleted roles

### Security Considerations
- All endpoints require ADMIN role authorization
- JWT token validation for all requests
- Input validation and sanitization
- Error handling with appropriate HTTP status codes

## 📊 Database Impact

### Tables Affected
- `users` - User information
- `roles` - Role definitions
- `users_roles` - Many-to-many relationship table

### Relationship Management
- Automatic management of user-role relationships
- Cascade operations for role assignments
- Referential integrity maintenance

## 🚨 Error Handling

### Common Error Scenarios
1. **User Not Found**: Returns 404 with appropriate message
2. **Role Not Found**: Returns 404 with role ID information
3. **Unauthorized Access**: Returns 403 for non-admin users
4. **Invalid Input**: Returns 400 with validation errors

### Error Response Format
```json
{
    "responseStatus": "ERROR",
    "status": "error",
    "message": "User with ID '999' not found",
    "data": null
}
```

## 🧪 Testing Scenarios

### Test Cases to Verify
1. Admin can assign multiple roles to user
2. Admin can remove specific roles from user
3. Admin can replace all user roles
4. Non-admin users cannot access role management endpoints
5. Invalid user/role IDs return appropriate errors
6. Role activation/deactivation works correctly

## 📈 Performance Considerations

### Optimizations Implemented
- Eager fetching of user roles to reduce database queries
- Batch processing for multiple role assignments
- Efficient Set operations for role management
- Indexed database queries for better performance

## 🔮 Future Enhancements

### Potential Improvements
1. **Role Hierarchy**: Implement parent-child role relationships
2. **Permission Management**: Fine-grained permission control
3. **Role Templates**: Pre-defined role combinations
4. **Bulk Operations**: Assign roles to multiple users simultaneously
5. **Role Expiration**: Time-based role assignments
6. **Approval Workflow**: Role assignment approval process

## 📝 Best Practices

### Implementation Guidelines
1. Always validate input data before processing
2. Use transactions for multiple database operations
3. Implement proper error handling and logging
4. Follow RESTful API conventions
5. Maintain consistent response formats
6. Document all API endpoints thoroughly

### Security Best Practices
1. Validate JWT tokens for all protected endpoints
2. Implement role-based access control consistently
3. Log all administrative actions for audit trails
4. Use HTTPS in production environments
5. Implement rate limiting for API endpoints
6. Validate and sanitize all input data

---

## 📞 Support

For questions or issues related to role management functionality, please refer to:
- Application logs for detailed error information
- API documentation for endpoint specifications
- Database schema for relationship understanding

**Author**: Vijay Rathod  
**Last Updated**: August 2025  
**Version**: 1.0
