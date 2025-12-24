# Role Management API Testing Examples

## 🚀 Quick Start Testing Guide

### Prerequisites
1. Application running on `http://localhost:9091`
2. Admin user credentials (username: admin, password: admin123)
3. API testing tool (Postman, curl, or similar)

## 📝 Step-by-Step Testing

### Step 1: Admin Login
```bash
curl -X POST http://localhost:9091/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

**Expected Response:**
```json
{
    "responseStatus": "OK",
    "status": "success",
    "message": "success",
    "data": {
        "jwtToken": "eyJhbGciOiJIUzM4NCJ9...",
        "user": {
            "id": 1,
            "name": "Admin User",
            "username": "admin",
            "roles": [{"name": "ROLE_ADMIN"}]
        }
    }
}
```

### Step 2: Get All Active Roles
```bash
curl -X GET http://localhost:9091/api/roles/active \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Step 3: Create a New Role
```bash
curl -X POST http://localhost:9091/api/roles \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "ROLE_MANAGER"
  }'
```

### Step 4: Assign Role to User
```bash
curl -X POST http://localhost:9091/api/roles/assign \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 2,
    "roleIds": [2, 3]
  }'
```

### Step 5: Get User Roles
```bash
curl -X GET http://localhost:9091/api/roles/user/2 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Step 6: Update Role Status
```bash
# Deactivate a role
curl -X PATCH http://localhost:9091/api/roles/3/deactivate \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Activate a role
curl -X PATCH http://localhost:9091/api/roles/3/activate \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Step 7: Replace User Roles
```bash
curl -X PUT http://localhost:9091/api/roles/replace \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 2,
    "roleIds": [1]
  }'
```

### Step 8: Remove Specific Roles
```bash
curl -X POST http://localhost:9091/api/roles/remove \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 2,
    "roleIds": [2]
  }'
```

## 🧪 Postman Collection

### Import this JSON into Postman for easy testing:

```json
{
    "info": {
        "name": "Role Management API",
        "description": "Testing role management endpoints"
    },
    "variable": [
        {
            "key": "baseUrl",
            "value": "http://localhost:9091"
        },
        {
            "key": "jwtToken",
            "value": "YOUR_JWT_TOKEN_HERE"
        }
    ],
    "item": [
        {
            "name": "Login Admin",
            "request": {
                "method": "POST",
                "header": [
                    {
                        "key": "Content-Type",
                        "value": "application/json"
                    }
                ],
                "body": {
                    "mode": "raw",
                    "raw": "{\n    \"username\": \"admin\",\n    \"password\": \"admin123\"\n}"
                },
                "url": {
                    "raw": "{{baseUrl}}/api/auth/login",
                    "host": ["{{baseUrl}}"],
                    "path": ["api", "auth", "login"]
                }
            }
        },
        {
            "name": "Get Active Roles",
            "request": {
                "method": "GET",
                "header": [
                    {
                        "key": "Authorization",
                        "value": "Bearer {{jwtToken}}"
                    }
                ],
                "url": {
                    "raw": "{{baseUrl}}/api/roles/active",
                    "host": ["{{baseUrl}}"],
                    "path": ["api", "roles", "active"]
                }
            }
        },
        {
            "name": "Assign Roles to User",
            "request": {
                "method": "POST",
                "header": [
                    {
                        "key": "Authorization",
                        "value": "Bearer {{jwtToken}}"
                    },
                    {
                        "key": "Content-Type",
                        "value": "application/json"
                    }
                ],
                "body": {
                    "mode": "raw",
                    "raw": "{\n    \"userId\": 2,\n    \"roleIds\": [1, 2]\n}"
                },
                "url": {
                    "raw": "{{baseUrl}}/api/roles/assign",
                    "host": ["{{baseUrl}}"],
                    "path": ["api", "roles", "assign"]
                }
            }
        }
    ]
}
```

## ✅ Expected Test Results

### Successful Role Assignment Response:
```json
{
    "responseStatus": "OK",
    "status": "success",
    "message": "success",
    "data": {
        "id": 2,
        "name": "John Doe",
        "username": "johndoe",
        "email": "john@example.com",
        "roles": [
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
}
```

### Error Response Example:
```json
{
    "responseStatus": "ERROR",
    "status": "error",
    "message": "User with ID '999' not found",
    "data": null
}
```

## 🔍 Troubleshooting

### Common Issues:

1. **401 Unauthorized**
   - Check if JWT token is valid and not expired
   - Ensure Bearer token format is correct

2. **403 Forbidden**
   - Verify user has ADMIN role
   - Check if user is authenticated properly

3. **404 Not Found**
   - Verify user/role IDs exist in database
   - Check endpoint URLs are correct

4. **400 Bad Request**
   - Validate JSON request body format
   - Check required fields are provided

## 📊 Database Verification

### Check role assignments in database:
```sql
-- View user roles
SELECT u.username, r.name as role_name 
FROM users u 
JOIN users_roles ur ON u.id = ur.user_id 
JOIN roles r ON ur.role_id = r.id 
WHERE u.id = 2;

-- View all roles
SELECT * FROM roles WHERE is_active = true AND is_deleted = false;
```

## 🎯 Testing Scenarios

### Scenario 1: New Employee Onboarding
1. Create user account
2. Assign basic USER role
3. Later promote to MANAGER role
4. Verify role changes take effect

### Scenario 2: Role Cleanup
1. Deactivate unused roles
2. Remove roles from departing employees
3. Verify access is properly restricted

### Scenario 3: Bulk Role Management
1. Assign multiple roles to single user
2. Replace all roles with new set
3. Remove specific roles while keeping others

---

**Note**: Replace `YOUR_JWT_TOKEN` with the actual JWT token received from login response.
