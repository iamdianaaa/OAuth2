# OAuth2
Authorization Server -  a system to manage user authentication and authorization

### Components

● Authorization Server: Manages user credentials, handles login requests, and grants access tokens.

● Client: Sends login requests to the Authorization Server and uses the access tokens to request resources.

● Resource Server: Validates access tokens received from clients and provides access to the requested resources.

### Architecture Diagram:

```
┌─────────────┐         ┌──────────────────┐
│   Client    │────────>│  Authorization   │
│ Application │<────────│     Server       │
└─────────────┘         └──────────────────┘
      │                          │
      │ Access Token             │ Validates
      │                          │ Credentials
      v                          v
┌─────────────┐         ┌──────────────────┐
│  Resource   │<────────│    PostgreSQL    │
│   Server    │         │     Database     │
└─────────────┘         └──────────────────┘
```



## Setup Instructions

### 1. Database Setup

```bash
# Install PostgreSQL (if not already installed)
# Ubuntu/Debian
sudo apt-get install postgresql postgresql-contrib

# macOS
brew install postgresql

# Start PostgreSQL service
sudo service postgresql start  # Linux
brew services start postgresql  # macOS

# Create database and tables
psql -U postgres
```

Then run the SQL script to create tables. The path for the script:

**client-app/database-setup.sql**


### 2. Build and Run Applications

**Terminal 1 - Authorization Server:**
```bash
cd oauth2-authorization-server
mvn clean install
mvn spring-boot:run
# Runs on http://localhost:8080
```

**Terminal 2 - Resource Server:**
```bash
cd oauth2-resource-server
mvn clean install
mvn spring-boot:run
# Runs on http://localhost:8081
```

**Terminal 3 - Client Application:**
```bash
cd oauth2-client-app
mvn clean install
mvn spring-boot:run
# Runs on http://localhost:8082
```


If you are using IntelliJ IDEA, then you can use pom.xml s for all three parts of this project to setup Maven projects using Tool Window.


## API Endpoints


The Postman collection for these endpoints are in this path:

**client-app/OAuth Server.postman_collection.json**




### Authorization Server (Port 8080)

#### 1. Register New User
```http
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "password": "securePassword123",
  "email": "john@example.com"
}
```

**Response:**
```json
{
  "message": "User registered successfully",
  "username": "john_doe"
}
```

#### 2. Login (Get Access Token)
```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "john_doe",
  "password": "securePassword123"
}
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

#### 3. Validate Token
```http
POST http://localhost:8080/api/auth/validate
Authorization: Bearer <your-token>
```

**Response:**
```json
{
  "valid": true,
  "username": "john_doe"
}
```

---

### Resource Server (Port 8081)

#### 1. Public Resource (No Authentication)
```http
GET http://localhost:8081/api/resources/public/info
```

**Response:**
```json
{
  "message": "This is a public resource, no authentication required",
  "timestamp": 1699200000000
}
```

#### 2. Protected Resource
```http
GET http://localhost:8081/api/resources/protected/data
Authorization: Bearer <your-token>
```

**Response:**
```json
{
  "message": "This is a protected resource. Access granted!",
  "user": "john_doe",
  "timestamp": "2024-11-05T10:30:00"
}
```

#### 3. Get User Info
```http
GET http://localhost:8081/api/resources/protected/user-info
Authorization: Bearer <your-token>
```

**Response:**
```json
{
  "username": "john_doe",
  "message": "User information retrieved successfully",
  "authorities": []
}
```

#### 4. Perform Action (POST)
```http
POST http://localhost:8081/api/resources/protected/action
Authorization: Bearer <your-token>
Content-Type: application/json

{
  "action": "updateProfile",
  "data": {
    "field": "email",
    "value": "newemail@example.com"
  }
}
```

**Response:**
```json
{
  "status": "success",
  "message": "Action performed successfully",
  "user": "john_doe",
  "payload": {
    "action": "updateProfile",
    "data": {
      "field": "email",
      "value": "newemail@example.com"
    }
  }
}
```

---

### Client Application (Port 8082)

#### 1. Client Login
```http
POST http://localhost:8082/client/login
Content-Type: application/json

{
  "username": "john_doe",
  "password": "securePassword123"
}
```

**Response:**
```json
{
  "message": "Login successful",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 3600
}
```

#### 2. Access Resource Through Client
```http
GET http://localhost:8082/client/access-resource
```

**Response:**
```json
{
  "message": "This is a protected resource. Access granted!",
  "user": "john_doe",
  "timestamp": "2024-11-05T10:30:00"
}
```

#### 3. Get User Info Through Client
```http
GET http://localhost:8082/client/user-info
```

#### 4. Perform Action Through Client
```http
POST http://localhost:8082/client/perform-action
Content-Type: application/json

{
  "action": "createDocument",
  "title": "My Document"
}
```

#### 5. Check Client Status
```http
GET http://localhost:8082/client/status
```

**Response:**
```json
{
  "clientActive": true,
  "hasToken": true,
  "message": "Client application is running"
}
```

#### 6. Client Logout
```http
POST http://localhost:8082/client/logout
```




## Troubleshooting

### Issue 1: Database Connection Error
```
Error: Could not connect to database
```
**Solution:**
- Check PostgreSQL is running: `sudo service postgresql status`
- Verify database exists: `psql -U postgres -l`
- Check credentials in `application.properties`

### Issue 2: Port Already in Use
```
Error: Port 8080 is already in use
```
**Solution:**
```bash
# Find process using port
lsof -i :8080

# Kill the process
kill -9 <PID>

# Or change port in application.properties
server.port=8083
```

### Issue 3: JWT Token Issues
```
Error: JWT signature does not match
```
**Solution:**
- Ensure `jwt.secret` is the same in both Authorization and Resource servers
- Check token hasn't expired
- Verify token format: `Bearer <token>`

### Issue 4: 403 Forbidden
```
Error: Access Denied
```
**Solution:**
- Verify token is not expired
- Check Authorization header format
- Ensure token was obtained from login endpoint
- Verify user exists in database



## Database Queries for Testing

### Check Users
```sql
SELECT * FROM users;
```

### Check Tokens
```sql
SELECT t.id, t.token, u.username, t.expires_at, t.created_at 
FROM access_tokens t 
JOIN users u ON t.user_id = u.id;
```

### Check User Roles
```sql
SELECT u.username, r.name 
FROM users u 
JOIN user_roles ur ON u.id = ur.user_id 
JOIN roles r ON ur.role_id = r.id;
```

### Clean Expired Tokens
```sql
DELETE FROM access_tokens WHERE expires_at < NOW();
```

### Reset User Password (for testing)
```sql
-- Password: password123
UPDATE users 
SET password = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy' 
WHERE username = 'testuser';
```
