# Task Management System

A RESTful API for managing tasks, built with Spring Boot with MySQL DB. This system allows users to create, assign, and track tasks with different priorities and statuses, featuring secure authentication and role-based access control.

## Features

- **Task Management**: Create, read, update, and delete tasks.
- **Task Filtering**: Filter tasks by priority, status, and due date.
- **User Management**: Admin-only user creation and listing.
- **Security**: JWT-based authentication and authorization.
- **Role-Based Access**: Distinguishes between `USER` and `ADMIN` roles.
- **Logging**: Comprehensive logging using SLF4J and Logback, with rolling file appenders.

## Technologies Used

- **Java 17**
- **Spring Boot 3.2.4**
- **Spring Data JPA**
- **Spring Security**
- **JWT (io.jsonwebtoken)**
- **MySQL** (Production)
- **H2** (Testing)
- **Maven**
- **Logback**

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL (for production environment)

### Installation

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd task-manager
   ```

2. Configure the database in `src/main/resources/application.properties`.

3. Build the project:
   ```bash
   mvn clean install
   ```

4. Run the application:
   ```bash
   mvn spring-boot:run
   ```

The API will be available at `http://localhost:8080`.

## API Endpoints

### Authentication
- `POST /api/auth/login`: Authenticate user and receive a JWT.
  - Request Body: `{"username": "...", "password": "..."}`

### Users (Admin Only)
- `POST /api/users`: Create a new user.
- `GET /api/users`: List all users.

### Tasks
- `GET /api/tasks`: Get all tasks. Supports filtering via query parameters:
  - `priority`: `LOW`, `MEDIUM`, `HIGH`
  - `status`: `TODO`, `IN_PROGRESS`, `DONE`
  - `dueDate`: `YYYY-MM-DD`
- `GET /api/tasks/{id}`: Get a specific task by ID.
- `POST /api/tasks`: Create a new task.
- `DELETE /api/tasks/{id}`: Delete a task.
- `PUT /api/tasks/{taskId}/assign/{userId}`: Assign a task to a user.

## Security

The application uses JWT for authentication. To access protected endpoints, include the token in the `Authorization` header:
`Authorization: Bearer <your_token>`

- **ADMIN role** is required for user management endpoints.
- **USER role** (or ADMIN) can manage tasks.

## Configuration

- **Database**: Configured in `src/main/resources/application.properties` for MySQL. H2 is used for tests (`src/test/resources/application.properties`).
- **Logging**: Logback configuration is in `src/main/resources/logback-spring.xml`. Logs are written to the console and `logs/app.log`.

## Testing

Run tests using Maven:
```bash
mvn clean test
```
