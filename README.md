# Video Generator Application

## Overview
A comprehensive Spring Boot application for generating video reels from multiple photos with fade transitions, JWT authentication, and complete REST API.

## Features

### 🎬 Video Management
- Create, read, update, and delete videos
- Video status tracking (PROCESSING, COMPLETED, FAILED, ARCHIVED)
- Soft delete functionality
- Metadata management (file size, duration)
- Pagination and sorting support

### 📤 File Upload
- Multi-file upload support
- File validation (extension, size)
- Upload history tracking
- Automatic directory management
- Upload statistics

### 🔐 Authentication & Security
- User registration and login
- JWT token-based authentication
- Role-based access control (USER, ADMIN, EDITOR)
- Password encryption with BCrypt
- Token refresh functionality
- CORS support

### 📊 Database
- Oracle Database integration
- JPA/Hibernate ORM
- Transaction management
- Connection pooling
- Migration support

### 🛠️ Additional Features
- Global exception handling
- Comprehensive logging (SLF4J)
- Request validation
- API documentation ready
- Environment-specific configurations
- File utility functions
- Input validation & sanitization

---

## Tech Stack

- **Language**: Java 17
- **Framework**: Spring Boot 3.1.5
- **Security**: Spring Security, JWT (JJWT)
- **Database**: Oracle Database
- **ORM**: Hibernate/JPA
- **Build Tool**: Maven
- **Logging**: SLF4J/Logback
- **Others**: Lombok, Jakarta EE

---

## Project Structure

```
src/main/java/com/videogenerator/
├── controller/           # REST API endpoints
│   ├── AuthController.java
│   ├── VideoController.java
│   └── UploadController.java
├── service/             # Business logic
│   ├── AuthService.java
│   ├── VideoService.java
│   └── UploadService.java
├── repository/          # Data access
│   ├── UserRepository.java
│   ├── VideoRepository.java
│   └── UploadHistoryRepository.java
├── entity/              # Database entities
│   ├── User.java
│   ├── Video.java
│   └── UploadHistory.java
├── model/               # DTOs and request/response objects
│   ├── AuthRequest.java
│   ├── AuthResponse.java
│   ├── VideoRequest.java
│   ├── ApiResponse.java
│   └── PaginationRequest.java
├── config/              # Configuration classes
│   ├── SecurityConfig.java
│   └── CustomUserDetailsService.java
├── util/                # Utility classes
│   ├── JwtUtil.java
│   ├── JwtAuthenticationFilter.java
│   ├── FileUtil.java
│   └── ValidationUtil.java
├── exception/           # Exception handling
│   └── GlobalExceptionHandler.java
└── VideoGeneratorApplication.java  # Main application class
```

---

## API Endpoints

### Authentication Endpoints

#### Register User
```
POST /api/auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "SecurePass123!",
  "fullName": "John Doe"
}

Response: 201 Created
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "token": "eyJhbGciOiJIUzUxMi...",
    "type": "Bearer",
    "userId": 1,
    "username": "john_doe",
    "email": "john@example.com"
  }
}
```

#### Login
```
POST /api/auth/login
Content-Type: application/json

{
  "username": "john_doe",
  "password": "SecurePass123!"
}

Response: 200 OK
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzUxMi...",
    "type": "Bearer",
    "userId": 1,
    "username": "john_doe",
    "email": "john@example.com"
  }
}
```

#### Get Current User
```
GET /api/auth/me
Authorization: Bearer {token}

Response: 200 OK
{
  "success": true,
  "message": "Current user retrieved",
  "data": {
    "id": 1,
    "username": "john_doe",
    "email": "john@example.com",
    "fullName": "John Doe",
    "isActive": true,
    "role": "USER"
  }
}
```

### Video Endpoints

#### Create Video
```
POST /api/videos
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "My Video",
  "description": "A video created from photos",
  "filePath": "/uploads/video1.mp4"
}

Response: 201 Created
```

#### Get All Videos (Paginated)
```
GET /api/videos?page=0&size=10&sortBy=createdAt&sortDirection=DESC
Authorization: Bearer {token}

Response: 200 OK
{
  "success": true,
  "data": {
    "content": [...],
    "pageable": {...},
    "totalElements": 50,
    "totalPages": 5
  }
}
```

#### Get Video by ID
```
GET /api/videos/{id}
Authorization: Bearer {token}

Response: 200 OK
```

#### Update Video
```
PUT /api/videos/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "Updated Title",
  "description": "Updated description"
}

Response: 200 OK
```

#### Delete Video
```
DELETE /api/videos/{id}
Authorization: Bearer {token}

Response: 200 OK
```

#### Get Video Status
```
GET /api/videos/{id}/status
Authorization: Bearer {token}

Response: 200 OK
```

### Upload Endpoints

#### Upload File
```
POST /api/uploads/video/{videoId}
Authorization: Bearer {token}
Content-Type: multipart/form-data

Form Data:
- file: <binary file>

Response: 201 Created
```

#### Get Upload History
```
GET /api/uploads/history?page=0&size=10
Authorization: Bearer {token}

Response: 200 OK
```

#### Get Video Upload History
```
GET /api/uploads/video/{videoId}/history
Authorization: Bearer {token}

Response: 200 OK
```

#### Delete Upload
```
DELETE /api/uploads/{id}
Authorization: Bearer {token}

Response: 200 OK
```

#### Get Upload Statistics
```
GET /api/uploads/stats
Authorization: Bearer {token}

Response: 200 OK
```

---

## Installation & Setup

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Oracle Database 11g or higher
- Git

### Steps

1. **Clone the repository**
```bash
git clone https://github.com/SAURABHINDURKAR/Video-Generator.git
cd Video-Generator
```

2. **Configure Database**
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:oracle:thin:@localhost:1521:XE
spring.datasource.username=your_username
spring.datasource.password=your_password
```

3. **Build the project**
```bash
mvn clean install
```

4. **Run the application**
```bash
# Development
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"

# Production
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=prod"

# Default
mvn spring-boot:run
```

5. **Access the application**
```
http://localhost:8080
```

---

## Configuration

### JWT Configuration
Edit `application.properties`:
```properties
jwt.secret=YourSecretKeyHere
jwt.expiration=86400000  # 24 hours in milliseconds
```

### File Upload Configuration
```properties
upload.directory=uploads
upload.max-file-size=104857600  # 100MB
```

### Database Connection
```properties
spring.datasource.url=jdbc:oracle:thin:@localhost:1521:XE
spring.datasource.username=videogen_user
spring.datasource.password=videogen_password
```

---

## Database Schema

### Users Table
```sql
CREATE TABLE users (
  id NUMBER PRIMARY KEY,
  username VARCHAR2(50) UNIQUE NOT NULL,
  email VARCHAR2(100) UNIQUE NOT NULL,
  password VARCHAR2(255) NOT NULL,
  full_name VARCHAR2(100),
  is_active CHAR(1) DEFAULT 'Y',
  role VARCHAR2(20) DEFAULT 'USER',
  created_at TIMESTAMP DEFAULT SYSDATE,
  updated_at TIMESTAMP DEFAULT SYSDATE
);
```

### Videos Table
```sql
CREATE TABLE videos (
  id NUMBER PRIMARY KEY,
  user_id NUMBER NOT NULL,
  title VARCHAR2(255) NOT NULL,
  description CLOB,
  file_path VARCHAR2(500) NOT NULL,
  file_size NUMBER,
  duration NUMBER,
  status VARCHAR2(20) DEFAULT 'PROCESSING',
  is_deleted CHAR(1) DEFAULT 'N',
  created_at TIMESTAMP DEFAULT SYSDATE,
  updated_at TIMESTAMP DEFAULT SYSDATE,
  deleted_at TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id)
);
```

### Upload History Table
```sql
CREATE TABLE upload_histories (
  id NUMBER PRIMARY KEY,
  video_id NUMBER NOT NULL,
  user_id NUMBER NOT NULL,
  file_name VARCHAR2(255) NOT NULL,
  file_path VARCHAR2(500) NOT NULL,
  file_size NUMBER,
  file_type VARCHAR2(100),
  upload_status VARCHAR2(20) DEFAULT 'PENDING',
  created_at TIMESTAMP DEFAULT SYSDATE,
  updated_at TIMESTAMP DEFAULT SYSDATE,
  FOREIGN KEY (video_id) REFERENCES videos(id),
  FOREIGN KEY (user_id) REFERENCES users(id)
);
```

---

## Testing

### Using Postman
1. Import the API collection
2. Set the authorization token in the header
3. Test each endpoint

### Example Test Flow
1. Register a new user
2. Login to get JWT token
3. Create a video
4. Upload files to the video
5. Query video status
6. Delete videos

---

## Error Handling

All errors follow a standard format:
```json
{
  "success": false,
  "message": "Error description",
  "data": null,
  "timestamp": "2026-09-27T12:00:00",
  "path": "/api/endpoint"
}
```

### Status Codes
- `200 OK` - Successful request
- `201 Created` - Resource created successfully
- `400 Bad Request` - Invalid request data
- `401 Unauthorized` - Missing or invalid authentication
- `403 Forbidden` - Insufficient permissions
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server error

---

## Logging

Logs are configured in `application.properties`:
```properties
logging.level.com.videogenerator=DEBUG
logging.level.org.springframework.security=DEBUG
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %msg%n
```

---

## Security Considerations

- All passwords are encrypted using BCrypt
- JWT tokens have configurable expiration
- CORS is configured for specific origins
- SQL injection prevention through parameterized queries
- Input validation and sanitization
- Role-based access control
- Soft delete for data retention

---

## Performance Optimization

- Connection pooling with HikariCP
- Database query optimization with pagination
- Lazy loading for entity relationships
- Indexed database columns
- Request/response compression
- Caching support ready

---

## Future Enhancements

- [ ] Video processing with FFmpeg
- [ ] Real-time progress tracking
- [ ] Advanced video editing features
- [ ] Social sharing integration
- [ ] Analytics dashboard
- [ ] Email notifications
- [ ] Subscription plans
- [ ] API rate limiting
- [ ] Swagger/OpenAPI documentation
- [ ] Docker containerization
- [ ] CI/CD pipeline
- [ ] Unit and integration tests

---

## Troubleshooting

### Database Connection Issues
```
Error: Unable to connect to Oracle Database
Solution: Check database URL, username, password, and Oracle service availability
```

### JWT Token Errors
```
Error: Invalid or expired token
Solution: Ensure token is valid, not expired, and correctly formatted in Authorization header
```

### File Upload Issues
```
Error: File size exceeds maximum limit
Solution: Increase upload.max-file-size in application.properties
```

---

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## Support

For support, email isaurabh36@gmail.com or open an issue on GitHub.

---

## Author

**Saurabh P. Indurkar**
- GitHub: [@SAURABHINDURKAR](https://github.com/SAURABHINDURKAR)
- Email: isaurabh36@gmail.com

---

## Changelog

### Version 1.0.0 (Initial Release)
- User authentication with JWT
- Video CRUD operations
- File upload management
- Role-based access control
- Comprehensive REST API
- Global exception handling
- Database integration with Oracle

---

**Last Updated**: 27 September 2026
