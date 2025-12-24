# Spring Boot User Master Application

## 📋 Project Overview

## 📖 API Documentation (Swagger / OpenAPI)

After starting the app, access interactive API docs here:

- Swagger UI: http://localhost:9091/swagger-ui/index.html
- OpenAPI JSON: http://localhost:9091/v3/api-docs
- OpenAPI YAML: http://localhost:9091/v3/api-docs.yaml

The **User Master Application** is a comprehensive user management system built with Spring Boot that provides enterprise-level user administration capabilities. This application implements JWT-based authentication, role-based access control, and advanced user management features including soft delete, pagination, filtering, and asynchronous processing.

## 🚀 Key Features

### 🔐 Authentication & Security
- **JWT Token Authentication**: Stateless authentication using JSON Web Tokens
- **Refresh Token Support**: Secure token refresh mechanism with configurable expiration
- **Role-Based Access Control (RBAC)**: Four distinct user roles with different permissions
- **Password Encryption**: Secure password storage using BCrypt hashing
- **Account Status Management**: Control user account activation and verification
- **Token Invalidation**: Secure logout by invalidating refresh tokens

### 👥 User Management
- **Complete CRUD Operations**: Create, Read, Update, Delete users
- **Profile Management**: Update personal information and preferences
- **Profile Picture Upload**: Support for user profile images with file validation
- **Account Status Management**: Toggle between active/inactive states
- **Soft Delete Pattern**: Mark users as deleted without losing data
- **Asynchronous Processing**: Non-blocking operations using CompletableFuture
- **Advanced Filtering**: Filter users by status, activity, and keywords
- **Pagination & Sorting**: Efficient data retrieval with customizable pagination
- **Account Status Control**: Activate/deactivate user accounts

### 🏗️ Architecture & Design Patterns
- **Layered Architecture**: Clean separation of concerns (Controller → Service → Repository → Entity)
- **DTO Pattern**: Data Transfer Objects for API communication
- **Auditing**: Automatic tracking of creation and modification timestamps
- **Exception Handling**: Centralized error handling and response formatting

## 🛠️ Technology Stack

- **Framework**: Spring Boot 3.x
- **Security**: Spring Security with JWT
- **Database**: JPA/Hibernate with MySQL/PostgreSQL
- **Build Tool**: Gradle
- **Logging**: Log4j2
- **Documentation**: Lombok for boilerplate code reduction

## 📊 Database Schema

### User Roles
- `ROLE_ADMIN` - Full system administration access
- `ROLE_SUPER_USER` - Extended user management capabilities
- `ROLE_NORMAL` - Standard user permissions
- `ROLE_WORKER` - Specialized worker-specific access

### Core Entities
- **User**: Main user entity with profile information and relationships
- **Role**: User permission levels and access control
- **AccountStatus**: Account verification and activation status
- **Worker**: Specialized user type for worker management

## 🌐 API Endpoints

### Authentication Endpoints
```
POST /api/auth/login          - User login (returns JWT token and refresh token)
POST /api/auth/register       - User registration
POST /api/auth/refresh-token  - Get new access token using refresh token
POST /api/auth/logout         - Invalidate refresh token
```

### User Management Endpoints
```
# User Operations
POST   /api/users             - Create new user
GET    /api/users             - Get all users with pagination and filters
GET    /api/users/{id}        - Get user by ID
PUT    /api/users/{id}        - Update user information
DELETE /api/users/{id}        - Soft delete user

# Profile Management
GET    /api/users/me          - Get current user's profile
PUT    /api/users/me          - Update current user's profile
PATCH  /api/users/me/status   - Update account status (active/inactive)
POST   /api/users/me/avatar   - Upload profile picture
DELETE /api/users/me/avatar   - Remove profile picture

# Token Management
POST   /api/tokens/refresh    - Refresh access token
POST   /api/tokens/revoke     - Revoke refresh token

# Filtering & Search
GET    /api/users/filter      - Advanced filtering with keyword search
GET    /api/users/active      - Get only active users
GET    /api/users/inactive    - Get only inactive users
```

### Worker User Management Endpoints
```
# Worker CRUD Operations
GET    /api/v1/workers                       - Get all workers with pagination
GET    /api/v1/workers/{id}                  - Get worker by ID
DELETE /api/v1/workers/{id}                  - Soft delete worker (move to recycle bin)
PATCH  /api/v1/workers/{id}/restore          - Restore worker from recycle bin
DELETE /api/v1/workers/{id}/permanent        - Permanently delete worker

# Worker Status Management
PATCH  /api/v1/workers/{id}/status           - Update worker account status (active/inactive)

# Worker Filtering & Search
GET    /api/v1/workers/active                - Get all active workers
GET    /api/v1/workers/pageable              - Get workers with pagination and sorting
GET    /api/v1/workers/search                - Search workers by keyword
GET    /api/v1/workers/recycle               - Get soft-deleted workers (recycle bin)
DELETE /api/v1/workers/recycle/delete-all    - Empty recycle bin (permanent delete all)

# SuperUser Worker Management
GET    /api/v1/workers/superuser/{superUserId}                    - Get workers by SuperUser ID
GET    /api/v1/workers/superuser/{superUserId}/filter             - Filter workers by type (all/active/deleted/expired)
GET    /api/v1/workers/superuser/{superUserId}/advanced-filter    - Advanced filtering with multiple criteria

# Favorite Worker Management
GET    /api/v1/workers/favorite-list         - Get user's favorite workers
POST   /api/v1/workers/favorite/{workerId}   - Add worker to favorites
DELETE /api/v1/workers/favorite/{id}         - Remove worker from favorites
```

### Email Verification & Account Management Endpoints
```
# Account Verification
GET    /api/v1/home/verify                    - Verify user account via email link
POST   /api/v1/home/unlock-account           - Unlock account with temporary password

# Password Management via Email
GET    /api/v1/home/send-email-reset         - Send password reset email
GET    /api/v1/home/verify-pswd-link         - Verify password reset link
POST   /api/v1/home/reset-password           - Reset password using email token
POST   /api/v1/home/forgot-password          - Request forgot password (generates temp password)

# Profile Password Management
PUT    /api/auth/change-password             - Change password (requires old password)
```

### Role Management Endpoints (Admin Only)
```
GET    /api/roles             - Get all roles
POST   /api/roles             - Create new role
GET    /api/roles/active      - Get all active roles
PATCH  /api/roles/{id}        - Update role details
PATCH  /api/roles/{id}/activate   - Activate role
PATCH  /api/roles/{id}/deactivate - Deactivate role
DELETE /api/roles/{id}        - Delete role

# User Role Assignment
POST   /api/roles/assign      - Assign roles to user
POST   /api/roles/remove      - Remove roles from user  
PUT    /api/roles/replace     - Replace all user roles
GET    /api/roles/user/{userId} - Get user's roles
GET    /api/roles/{id}/exists - Check if role exists
```

### Filtering Options
- **All users**: `/api/users/filter`
- **Active users**: `/api/users/filter?isDeleted=false&isActive=true`
- **Deleted users**: `/api/users/filter?isDeleted=true`
- **Expired users**: `/api/users/filter?isDeleted=false&isActive=false`

## 🔧 Configuration

### Application Properties
```properties
# Server & Database
server.port=9091
spring.datasource.url=jdbc:mysql://localhost:3306/user_master
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update

# JWT Configuration
jwt.secret=your-secret-key-here
jwt.expiration=900000  # 15 minutes in milliseconds
jwt.refreshExpiration=604800000  # 7 days in milliseconds

# File Upload
spring.servlet.multipart.max-file-size=2MB
spring.servlet.multipart.max-request-size=2MB

# Security
security.jwt.header=Authorization
security.jwt.prefix=Bearer 
security.jwt.refresh.header=X-Refresh-Token
```

### JWT Configuration
- Token expiration: 7 days
- Algorithm: HS384
- Automatic token validation on protected endpoints

## 🚦 Getting Started

### Prerequisites
- Java 17 or higher
- MySQL/PostgreSQL database
- Gradle 7.x or higher

### Installation Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/RathodVijay9524/User-Master.git
   cd User-Master
   ```

2. **Configure database**
   - Update `application.properties` with your database credentials
   - Create database: `CREATE DATABASE user_master;`

3. **Build and run**
   ```bash
   ./gradlew build
   ./gradlew bootRun
   ```

4. **Access the application**
   - Base URL: `http://localhost:9091`
   - API Documentation (Swagger UI): `http://localhost:9091/swagger-ui/index.html`
   - OpenAPI JSON: `http://localhost:9091/v3/api-docs`
   - OpenAPI YAML: `http://localhost:9091/v3/api-docs.yaml`

## 📝 Usage Examples

### User Login with JWT and Refresh Token
```bash
POST http://localhost:9091/api/auth/login
Content-Type: application/json

{
  "usernameOrEmail": "rathod",
  "password": "rathod"
}
```

### Refresh Access Token
```bash
POST http://localhost:9091/api/auth/refresh-token
Content-Type: application/json

{
  "refreshToken": "your-refresh-token-here"
}
```

### Upload Profile Picture
```bash
POST http://localhost:9091/api/users/me/avatar
Content-Type: multipart/form-data
Authorization: Bearer your-jwt-token

# Form data:
# file: [select your image file]
```

### Update User Status
```bash
PATCH http://localhost:9091/api/users/me/status
Content-Type: application/json
Authorization: Bearer your-jwt-token

{
  "active": true
}
```

### User Registration with Email Verification
```bash
POST http://localhost:9091/api/auth/register/admin
Content-Type: application/json

{
  "name": "John Doe",
  "username": "johndoe",
  "email": "john@example.com",
  "password": "securePassword123"
}
```

### Account Verification (via email link)
```bash
GET http://localhost:9091/api/v1/home/verify?uid=123&code=verification-code-from-email
```

### Forgot Password Request
```bash
POST http://localhost:9091/api/v1/home/forgot-password?usernameOrEmail=john@example.com
Content-Type: application/json

{
  "email": "john@example.com"
}
```

### Send Password Reset Email
```bash
GET http://localhost:9091/api/v1/home/send-email-reset?email=john@example.com
```

### Reset Password via Email Token
```bash
POST http://localhost:9091/api/v1/home/reset-password
Content-Type: application/json

{
  "uid": 123,
  "token": "reset-token-from-email",
  "newPassword": "newSecurePassword123",
  "confirmPassword": "newSecurePassword123"
}
```

### Change Password (Profile Management)
```bash
PUT http://localhost:9091/api/auth/change-password
Content-Type: application/json
Authorization: Bearer your-jwt-token

{
  "username": "johndoe",
  "oldPassword": "currentPassword123",
  "newPassword": "newSecurePassword456"
}
```

### Unlock Account with Temporary Password
```bash
POST http://localhost:9091/api/v1/home/unlock-account?usernameOrEmail=john@example.com
Content-Type: application/json

{
  "username": "johndoe",
  "tempPassword": "temp-password-from-email",
  "newPassword": "newSecurePassword789"
}
```

### Create User
```bash
POST http://localhost:9091/api/users
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "name": "John Doe",
  "username": "johndoe",
  "email": "john@example.com",
  "password": "securePassword123"
}
```

### Get Users with Filters
```bash
GET http://localhost:9091/api/users/filter?isActive=true&pageNumber=0&pageSize=10&sortBy=name&sortDir=asc
Authorization: Bearer {jwt_token}
```

## 🏆 Advanced Features

### Asynchronous Processing
- Non-blocking user creation and updates
- Improved application performance and scalability
- CompletableFuture implementation for async operations

### Soft Delete Implementation
- Users are marked as deleted rather than physically removed
- Maintains data integrity and audit trails
- Ability to restore deleted users

### Comprehensive Filtering
- Filter by active/inactive status
- Filter by deleted/non-deleted users
- Keyword search across user fields
- Combinable filters for complex queries

### Pagination & Sorting
- Configurable page size and number
- Multi-field sorting capabilities
- Performance optimized for large datasets

## 📧 Email Verification & Account Management Workflows

### 🔐 User Registration with Email Verification Flow
1. **User Registration**: User submits registration form via `/api/auth/register/admin`
2. **Account Creation**: System creates user account with `INACTIVE` status
3. **Email Sent**: Verification email sent to user with activation link
4. **Email Content**: Contains link like `http://localhost:5173/verify?uid=123&code=verification-code`
5. **User Clicks Link**: Redirects to frontend verification page
6. **Backend Verification**: Frontend calls `/api/v1/home/verify?uid=123&code=verification-code`
7. **Account Activated**: User account status changed to `ACTIVE`

### 🔑 Forgot Password & Reset Flow
1. **Password Reset Request**: User requests password reset via `/api/v1/home/send-email-reset?email=user@example.com`
2. **Email Sent**: Password reset email sent with secure token
3. **Email Content**: Contains reset link with token
4. **User Clicks Link**: Redirects to password reset form
5. **Link Verification**: System verifies token via `/api/v1/home/verify-pswd-link`
6. **Password Reset**: User submits new password via `/api/v1/home/reset-password`
7. **Password Updated**: System updates password and invalidates token

### 🛡️ Account Unlock with Temporary Password Flow
1. **Forgot Password**: User requests forgot password via `/api/v1/home/forgot-password`
2. **Temporary Password**: System generates and emails temporary password
3. **Account Unlock**: User uses temp password to unlock account via `/api/v1/home/unlock-account`
4. **New Password**: User sets new permanent password during unlock process

### 🔧 Profile Password Change Flow
1. **Authentication Required**: User must be logged in with valid JWT token
2. **Password Change**: User submits old and new password via `/api/auth/change-password`
3. **Validation**: System validates old password before updating
4. **Password Updated**: New password encrypted and stored

## 📋 Response Examples

### Registration Success Response
```json
{
    "responseStatus": "OK",
    "status": "success",
    "message": "Your account register successfully. verify & Active your account",
    "data": null
}
```

### Login Success Response
```json
{
    "responseStatus": "OK",
    "status": "success",
    "message": "Login successful",
    "data": {
        "jwtToken": "eyJhbGciOiJIUzM4NCJ9...",
        "refreshToken": {
            "token": "eyJhbGciOiJIUzM4NCJ9...",
            "expiryDate": "2025-08-15T12:34:56.789Z"
        },
        "user": {
            "id": 4,
            "name": "Vijay Rathod",
            "username": "rathod",
            "email": "rathod@gmail.com",
            "profileImage": "/api/users/4/avatar",
            "roles": [
                {
                    "name": "ROLE_USER",
                    "id": 2,
                    "active": true,
                    "deleted": false
                }
            ],
            "active": true,
            "deleted": false,
            "lastLogin": "2025-08-08T12:34:56.789Z"
        }
    }
}
```

### Account Verification Success Response
```json
{
    "status": "success",
    "message": "Account verified successfully!"
}
```

### Account Verification Error Response
```json
{
    "status": "error",
    "message": "Invalid or expired verification link."
}
```

### Password Reset Email Sent Response
```json
{
    "responseStatus": "OK",
    "status": "success",
    "message": "Email Send Success !! Check Email Reset Password"
}
```

### Password Reset Success Response
```json
{
    "responseStatus": "OK",
    "status": "success",
    "message": "Password reset successfully"
}
```

### Password Change Success Response
```json
{
    "responseStatus": "OK",
    "status": "success",
    "message": "Password Changed. !!"
}
```

### Token Refresh Response
```json
{
    "accessToken": "new-jwt-token-here",
    "refreshToken": "new-refresh-token-here",
    "tokenType": "Bearer"
}
```

### Profile Update Response
```json
{
    "id": 4,
    "name": "Vijay Rathod Updated",
    "username": "rathod",
    "email": "rathod.updated@example.com",
    "profileImage": "/api/users/4/avatar",
    "status": "ACTIVE",
    "lastModifiedDate": "2025-08-08T13:45:00.000Z"
}
```

## 🔍 Project Structure

```
src/main/java/com/vijay/User_Master/
├── controller/          # REST API controllers
├── service/            # Business logic layer
├── repository/         # Data access layer
├── entity/            # JPA entities
├── dto/               # Data Transfer Objects
├── config/            # Configuration classes
├── exceptions/        # Custom exception handling
└── Helper/            # Utility classes
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**Vijay Rathod**
- GitHub: [@RathodVijay9524](https://github.com/RathodVijay9524)
- Email: rathod@gmail.com

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- Spring Security for robust authentication mechanisms
- All contributors who helped improve this project

## 📝 Worker Management Examples

### Get Workers by SuperUser with Advanced Filtering
```bash
# Get all workers created by SuperUser
GET http://localhost:9091/api/v1/workers/superuser/123/advanced-filter

# Get only active workers
GET http://localhost:9091/api/v1/workers/superuser/123/advanced-filter?isActive=true

# Get only deleted workers
GET http://localhost:9091/api/v1/workers/superuser/123/advanced-filter?isDeleted=true

# Search workers by keyword
GET http://localhost:9091/api/v1/workers/superuser/123/advanced-filter?keyword=john

# Combine filters: active workers with keyword search
GET http://localhost:9091/api/v1/workers/superuser/123/advanced-filter?isActive=true&keyword=developer

# Get expired workers (not deleted but inactive)
GET http://localhost:9091/api/v1/workers/superuser/123/advanced-filter?isDeleted=false&isActive=false

# With pagination
GET http://localhost:9091/api/v1/workers/superuser/123/advanced-filter?page=1&size=5
```

### Filter Workers by Type
```bash
# Get all workers
GET http://localhost:9091/api/v1/workers/superuser/123/filter?type=all

# Get active workers only
GET http://localhost:9091/api/v1/workers/superuser/123/filter?type=active

# Get deleted workers only
GET http://localhost:9091/api/v1/workers/superuser/123/filter?type=deleted

# Get expired workers only
GET http://localhost:9091/api/v1/workers/superuser/123/filter?type=expired
```

### Favorite Worker Management
```bash
# Add worker to favorites
POST http://localhost:9091/api/v1/workers/favorite/456
Authorization: Bearer your-jwt-token

# Remove worker from favorites
DELETE http://localhost:9091/api/v1/workers/favorite/456
Authorization: Bearer your-jwt-token

# Get user's favorite workers
GET http://localhost:9091/api/v1/workers/favorite-list
Authorization: Bearer your-jwt-token
```

### Worker Status Management
```bash
# Update worker account status
PATCH http://localhost:9091/api/v1/workers/456/status?isActive=true
Authorization: Bearer your-jwt-token

# Soft delete worker (move to recycle bin)
DELETE http://localhost:9091/api/v1/workers/456
Authorization: Bearer your-jwt-token

# Restore worker from recycle bin
PATCH http://localhost:9091/api/v1/workers/456/restore
Authorization: Bearer your-jwt-token

# Permanently delete worker
DELETE http://localhost:9091/api/v1/workers/456/permanent
Authorization: Bearer your-jwt-token
```

### Worker Search and Pagination
```bash
# Search workers by query
GET http://localhost:9091/api/v1/workers/search?query=developer&page=0&size=10&sort=name,asc
Authorization: Bearer your-jwt-token

# Get workers with pagination and sorting
GET http://localhost:9091/api/v1/workers/pageable?pageNumber=0&pageSize=10&sortBy=name&sortDir=asc
Authorization: Bearer your-jwt-token

# Get recycle bin (soft-deleted workers)
GET http://localhost:9091/api/v1/workers/recycle?page=0&size=10&sort=name
Authorization: Bearer your-jwt-token
```
