# SkillSwap

A peer-to-peer skill exchange platform developed as a college OOP project.

SkillSwap lets users register, log in, list skills they can teach and skills they want to learn, and send skill exchange offers to other users. The backend is a Spring Boot REST API backed by PostgreSQL. The frontend is a React + Vite application with Tailwind CSS styling.

This is an **academic project and local prototype**. It is not a production-grade enterprise application, and it does not use AI, machine learning, or complex recommendation algorithms.

---

## Table of Contents

- [Project Overview](#project-overview)
- [Core Features](#core-features)
- [Technology Stack](#technology-stack)
- [Backend Architecture](#backend-architecture)
- [Project Directory Structure](#project-directory-structure)
- [Database Schema and Entities](#database-schema-and-entities)
- [API Documentation](#api-documentation)
- [Swagger API Documentation](#swagger-api-documentation)
- [Project Setup Instructions](#project-setup-instructions)
- [Testing Instructions](#testing-instructions)
- [Current Project Status](#current-project-status)
- [Future Scope](#future-scope)
- [Contributors](#contributors)

---

## Project Overview

SkillSwap is a simple peer-to-peer skill exchange platform. A user who knows Java and wants to learn React can discover another user who knows React and wants to learn Java, then initiate a skill exchange.

The platform allows users to:

- Register and log in.
- Add skills they can teach (TEACH) and skills they want to learn (LEARN) to their profile.
- View their own skill list.
- Remove skill associations from their profile.

This is a college OOP project focused on clean Java object-oriented design, layered backend architecture, and REST API implementation.

---

## Core Features

### Implemented

- User registration with BCrypt password hashing.
- User login with BCrypt password verification (stateless credential check; no JWT or session token issued).
- Duplicate email rejection during registration.
- Add a skill to a user's profile (reusing an existing shared `Skill` record when name and category match).
- Retrieve all skills associated with a user.
- Delete a user-skill association (does not delete the shared `Skill` record).
- Health-check endpoint (`GET /`).
- Swagger UI for API exploration and testing.

### Not Yet Implemented

- User discovery / browsing other users' profiles.
- Exchange offer creation, acceptance, or rejection (the `ExchangeOffer` entity exists but has no controller or API).
- Real-time chat.
- Session scheduling.
- Reviews and feedback.
- Frontend-to-backend API integration (the frontend currently uses mock data).
- Password reset and email verification.

---

## Technology Stack

The following technologies are actually present in the project:

| Layer | Technology | Purpose |
|-------|-----------|---------|
| Language | Java 25 | Backend application code |
| Backend Framework | Spring Boot 4.1.2 | Application framework |
| Web Layer | Spring Web MVC | REST controllers and HTTP handling |
| Persistence | Spring Data JPA | Repository abstraction |
| ORM | Hibernate | Object-relational mapping |
| Database | PostgreSQL 17 | Persistent data storage |
| Database Driver | PostgreSQL JDBC | Java-to-database connectivity |
| Build Tool | Apache Maven | Dependency and build management |
| Password Hashing | spring-security-crypto (BCrypt) | Secure password storage |
| API Documentation | springdoc-openapi 3.2.0 | Swagger UI and OpenAPI docs |
| Frontend | React | User interface |
| Frontend Tooling | Vite | Development server and bundler |
| Frontend Styling | Tailwind CSS | Utility-first CSS |
| Frontend Icons | lucide-react | Icon library |

**Note:** The `spring-boot-starter-websocket` dependency is declared in `pom.xml`, but no WebSocket controllers or chat endpoints are implemented. The frontend does not currently call any backend API endpoints; it uses hardcoded mock data.

---

## Backend Architecture

The backend follows a layered Spring Boot architecture:

```text
Client (Browser / Swagger UI / Frontend)
        |
   HTTP / REST
        |
+---------------------+
|   Controller Layer  |  Handles HTTP requests and responses
+---------------------+
|    Service Layer    |  Contains business logic
+---------------------+
|  Repository Layer   |  Spring Data JPA data access
+---------------------+
|   Model / Entity    |  JPA entities mapped to DB tables
+---------------------+
|       DTO Layer     |  Request and response data objects
+---------------------+
        |
   PostgreSQL
```

### Controller Layer

Receives HTTP requests, validates input, calls services, and returns `ResponseEntity` responses with appropriate HTTP status codes. Controllers do not contain business logic.

### Service Layer

Contains business logic such as checking for duplicate emails, hashing passwords, verifying credentials, reusing existing `Skill` records, and enforcing unique skill associations per user.

### Repository Layer

Spring Data JPA interfaces that extend `JpaRepository` and provide database access. Custom query methods are declared for lookups by email, user ID, and skill name/category.

### Model / Entity Layer

JPA entities annotated with `@Entity` that map to PostgreSQL tables. Fields are encapsulated with getters and setters.

### DTO Layer

Plain Java objects used to transfer request and response data between the client and the server. DTOs use Jakarta Bean Validation annotations (`@NotBlank`, `@Email`, `@Size`, `@NotNull`).

---

## Project Directory Structure

```text
Skill-Swap/
├── backend/
│   ├── .mvn/wrapper/
│   ├── database/
│   │   └── queries.sql
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/backend/
│   │   │   │   ├── BackendApplication.java
│   │   │   │   ├── config/
│   │   │   │   │   └── PasswordConfig.java
│   │   │   │   ├── controller/
│   │   │   │   │   ├── AuthController.java
│   │   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   │   ├── SkillController.java
│   │   │   │   │   └── TestController.java
│   │   │   │   ├── dto/
│   │   │   │   │   ├── AddSkillRequest.java
│   │   │   │   │   ├── AuthResponse.java
│   │   │   │   │   ├── LoginRequest.java
│   │   │   │   │   └── RegisterRequest.java
│   │   │   │   ├── model/
│   │   │   │   │   ├── ExchangeOffer.java
│   │   │   │   │   ├── OfferStatus.java
│   │   │   │   │   ├── Skill.java
│   │   │   │   │   ├── SkillType.java
│   │   │   │   │   ├── User.java
│   │   │   │   │   └── UserSkill.java
│   │   │   │   ├── repository/
│   │   │   │   │   ├── SkillRepository.java
│   │   │   │   │   ├── UserRepository.java
│   │   │   │   │   └── UserSkillRepository.java
│   │   │   │   └── service/
│   │   │   │       ├── AuthService.java
│   │   │   │       ├── EmailAlreadyExistsException.java
│   │   │   │       ├── InvalidCredentialsException.java
│   │   │   │       └── SkillService.java
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/
│   │       └── java/com/example/backend/
│   │           └── BackendApplicationTests.java
│   ├── HELP.md
│   ├── mvnw
│   ├── mvnw.cmd
│   └── pom.xml
├── frontend/
│   ├── index.html
│   ├── package.json
│   ├── package-lock.json
│   ├── postcss.config.js
│   ├── tailwind.config.js
│   ├── vite.config.js
│   └── src/
│       ├── App.jsx
│       ├── main.jsx
│       └── styles.css
└── README.md
```

**Package:** `com.example.backend`

---

## Database Schema and Entities

The database name is `skill-swap`. Hibernate is configured with `ddl-auto=update`, so tables are created automatically on application startup.

### Entity: User

Table: `users`

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| `id` | `Long` | `@Id`, auto-generated | Primary key |
| `name` | `String` | `nullable = false`, length 100 | User's display name |
| `email` | `String` | `nullable = false`, `unique = true`, length 255 | User's email address |
| `passwordHash` | `String` | `nullable = false`, length 255 | BCrypt-hashed password |
| `bio` | `String` | length 1000 | Optional user biography |

A unique constraint `uk_users_email` enforces unique email addresses.

### Entity: Skill

Table: `skills`

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| `id` | `Long` | `@Id`, auto-generated | Primary key |
| `name` | `String` | `nullable = false`, length 100 | Skill name (e.g. "Java") |
| `category` | `String` | length 100 | Skill category (e.g. "Programming") |

Skills are shared across users. The repository looks up existing skills by name and category (case-insensitive) before creating a new one.

### Entity: UserSkill

Table: `user_skills`

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| `id` | `Long` | `@Id`, auto-generated | Primary key |
| `user` | `User` | `@ManyToOne`, `nullable = false` | The user who owns this skill association |
| `skill` | `Skill` | `@ManyToOne`, `nullable = false` | The skill being associated |
| `type` | `SkillType` | `@Enumerated(STRING)`, `nullable = false` | `TEACH` or `LEARN` |

A unique constraint `uk_user_skill_type` prevents duplicate associations of the same user, skill, and type.

### Entity: ExchangeOffer

Table: `exchange_offers`

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| `id` | `Long` | `@Id`, auto-generated | Primary key |
| `sender` | `User` | `@ManyToOne`, `nullable = false` | User sending the offer |
| `receiver` | `User` | `@ManyToOne`, `nullable = false` | User receiving the offer |
| `offeredSkill` | `Skill` | `@ManyToOne`, `nullable = false` | Skill the sender offers to teach |
| `requestedSkill` | `Skill` | `@ManyToOne`, `nullable = false` | Skill the sender wants to learn |
| `status` | `OfferStatus` | `@Enumerated(STRING)`, `nullable = false`, default `PENDING` | Offer lifecycle status |
| `createdAt` | `LocalDateTime` | `nullable = false` | Timestamp when the offer was created (set via `@PrePersist`) |

**Note:** The `ExchangeOffer` entity exists but has **no controller, service, or API endpoints** implemented. It is part of the data model for future work.

### Enum: SkillType

Values: `TEACH`, `LEARN`

Indicates whether a user can teach a skill or wants to learn it.

### Enum: OfferStatus

Values: `PENDING`, `ACCEPTED`, `REJECTED`

Represents the lifecycle state of an exchange offer.

---

## API Documentation

All backend endpoints are under the base URL `http://localhost:8080`.

### Endpoint: GET /

| Property | Value |
|----------|-------|
| Method | `GET` |
| URL | `/` |
| Purpose | Health check to confirm the backend is running. |
| Response | Plain text: `SkillSwap Backend is running!` |
| Success Status | `200 OK` |

---

### Endpoint: POST /api/auth/register

| Property | Value |
|----------|-------|
| Method | `POST` |
| URL | `/api/auth/register` |
| Purpose | Register a new user account. Password is hashed with BCrypt before storage. |
| Request Body | `RegisterRequest` |

**Example Request:**

```json
{
  "name": "Rohan",
  "email": "rohan@example.com",
  "password": "password123",
  "bio": "Android and AI developer"
}
```

**Success Response (`201 Created`):**

```json
{
  "success": true,
  "message": "Registration successful",
  "id": 1,
  "name": "Rohan",
  "email": "rohan@example.com",
  "bio": "Android and AI developer"
}
```

**Error Responses:**

| Status | Condition |
|--------|-----------|
| `400 Bad Request` | Validation error (missing name, invalid email, password shorter than 6 characters). |
| `409 Conflict` | Email already registered. |

---

### Endpoint: POST /api/auth/login

| Property | Value |
|----------|-------|
| Method | `POST` |
| URL | `/api/auth/login` |
| Purpose | Verify user credentials against the stored BCrypt hash. This MVP verifies credentials but does **not** issue a persistent authentication token or establish a secured session. |
| Request Body | `LoginRequest` |

**Example Request:**

```json
{
  "email": "rohan@example.com",
  "password": "password123"
}
```

**Success Response (`200 OK`):**

```json
{
  "success": true,
  "message": "Login successful",
  "id": 1,
  "name": "Rohan",
  "email": "rohan@example.com",
  "bio": "Android and AI developer"
}
```

**Error Responses:**

| Status | Condition |
|--------|-----------|
| `400 Bad Request` | Validation error (missing email, invalid email format, missing password). |
| `401 Unauthorized` | Invalid email or password. |

**Note:** The `passwordHash` field is never included in any API response.

---

### Endpoint: POST /api/users/{userId}/skills

| Property | Value |
|----------|-------|
| Method | `POST` |
| URL | `/api/users/{userId}/skills` |
| Purpose | Add a skill to a user's profile. Reuses an existing `Skill` record when name and category match (case-insensitive). Prevents duplicate user-skill-type associations. |
| Path Parameter | `userId` (Long) — ID of the user |
| Request Body | `AddSkillRequest` |

**Example Request:**

```json
{
  "name": "Java",
  "category": "Programming",
  "type": "TEACH"
}
```

`type` must be either `TEACH` or `LEARN`.

**Success Response (`201 Created`):**

Returns the created `UserSkill` object:

```json
{
  "id": 1,
  "user": { "id": 1, "name": "Rohan", ... },
  "skill": { "id": 1, "name": "Java", "category": "Programming" },
  "type": "TEACH"
}
```

**Error Responses:**

| Status | Condition |
|--------|-----------|
| `400 Bad Request` | Validation error (missing name, category, or type). |
| `404 Not Found` | User with the given `userId` does not exist. |
| `409 Conflict` | The user already has this skill with this type. |

---

### Endpoint: GET /api/users/{userId}/skills

| Property | Value |
|----------|-------|
| Method | `GET` |
| URL | `/api/users/{userId}/skills` |
| Purpose | Retrieve all skills associated with a particular user. |
| Path Parameter | `userId` (Long) — ID of the user |

**Success Response (`200 OK`):**

Returns a list of `UserSkill` objects:

```json
[
  {
    "id": 1,
    "user": { "id": 1, "name": "Rohan", ... },
    "skill": { "id": 1, "name": "Java", "category": "Programming" },
    "type": "TEACH"
  },
  {
    "id": 2,
    "user": { "id": 1, "name": "Rohan", ... },
    "skill": { "id": 2, "name": "React", "category": "Frontend" },
    "type": "LEARN"
  }
]
```

**Error Responses:**

| Status | Condition |
|--------|-----------|
| `404 Not Found` | User with the given `userId` does not exist. |

---

### Endpoint: DELETE /api/users/{userId}/skills/{userSkillId}

| Property | Value |
|----------|-------|
| Method | `DELETE` |
| URL | `/api/users/{userId}/skills/{userSkillId}` |
| Purpose | Remove the association between a user and a skill. Does **not** delete the shared `Skill` record itself. |
| Path Parameters | `userId` (Long) — ID of the user<br>`userSkillId` (Long) — ID of the `UserSkill` association |

**Success Response (`204 No Content`):**

No response body.

**Error Responses:**

| Status | Condition |
|--------|-----------|
| `404 Not Found` | The skill association was not found for this user. |

---

## Swagger API Documentation

Swagger UI is configured via `springdoc-openapi-starter-webmvc-ui`.

When the application is running, access the interactive API documentation at:

```
http://localhost:8080/swagger-ui/index.html
```

The OpenAPI specification is available at:

```
http://localhost:8080/v3/api-docs
```

### How to Use Swagger UI

1. Start the backend application (see [Project Setup Instructions](#project-setup-instructions)).
2. Open `http://localhost:8080/swagger-ui/index.html` in a browser.
3. Expand the `AuthController` section to test:
   - `POST /api/auth/register`
   - `POST /api/auth/login`
4. Expand the `SkillController` section to test:
   - `POST /api/users/{userId}/skills`
   - `GET /api/users/{userId}/skills`
   - `DELETE /api/users/{userId}/skills/{userSkillId}`
5. Click **Try it out**, edit the example JSON request body, and click **Execute**.
6. Review the response code, response body, and headers.

---

## Project Setup Instructions

### Prerequisites

- **Java JDK 25** (configured in `backend/pom.xml` as `java.version`)
- **Apache Maven 3.9.x**
- **PostgreSQL 17**
- A Java IDE (e.g., IntelliJ IDEA, Eclipse, VS Code) — optional
- **Git**

### 1. Clone the Repository

```powershell
git clone <repository-url>
cd Skill-Swap
```

### 2. Configure PostgreSQL

Install PostgreSQL 17 and start the service. Create the `skill-swap` database:

```powershell
# Connect to PostgreSQL as the postgres user, then run:
CREATE DATABASE "skill-swap";
```

### 3. Configure Backend Database Connection

Edit `backend/src/main/resources/application.properties`. The file already contains:

```properties
spring.application.name=backend

spring.datasource.url=jdbc:postgresql://localhost:5432/skill-swap
spring.datasource.username=postgres
spring.datasource.password=8727

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

Update `spring.datasource.username` and `spring.datasource.password` to match your local PostgreSQL credentials. Use placeholders or environment-specific values; do not commit real passwords to version control.

### 4. Build and Run the Backend

Open a terminal in the `backend` directory:

```powershell
cd backend
```

Using the Maven Wrapper:

```powershell
.\mvnw.cmd spring-boot:run
```

Or, if Maven is installed globally:

```powershell
mvn spring-boot:run
```

To compile only:

```powershell
.\mvnw.cmd compile
```

The backend starts at `http://localhost:8080`.

### 5. Run the Frontend

Open a separate terminal in the `frontend` directory:

```powershell
cd frontend
npm install
npm run dev
```

Vite will print a local URL, commonly `http://localhost:5173`.

**Note:** The frontend currently uses mock data and does not call backend API endpoints. Backend API testing should be done through Swagger UI.

---

## Testing Instructions

Use Swagger UI to test the implemented APIs:

### 1. Register a User

Open `POST /api/auth/register` in Swagger UI and execute:

```json
{
  "name": "user",
  "email": "user@example.com",
  "password": "password123",
  "bio": "Android and AI developer"
}
```

Expected: `201 Created` with the user profile (no `passwordHash`).

### 2. Verify the User in PostgreSQL

Connect to the `skill-swap` database and confirm the row exists:

```sql
SELECT id, name, email, bio FROM users WHERE email = 'user@example.com';
```

Confirm the `password_hash` column contains a BCrypt hash (starts with `$2a$` or `$2b$`), not plain text.

### 3. Test Duplicate Email Registration

Repeat the registration request with the same email.

Expected: `409 Conflict`.

### 4. Test Successful Login

Open `POST /api/auth/login` and execute:

```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

Expected: `200 OK` with the user profile.

### 5. Test Incorrect Password Login

Use the same email with an incorrect password.

Expected: `401 Unauthorized`.

### 6. Add a Skill (TEACH)

Open `POST /api/users/{userId}/skills` and execute (replace `{userId}` with the ID from registration):

```json
{
  "name": "Java",
  "category": "Programming",
  "type": "TEACH"
}
```

Expected: `201 Created`.

### 7. Add a Skill (LEARN)

```json
{
  "name": "React",
  "category": "Frontend",
  "type": "LEARN"
}
```

Expected: `201 Created`.

### 8. Retrieve the User's Skills

Open `GET /api/users/{userId}/skills` and execute.

Expected: `200 OK` with a list of the user's skills.

### 9. Delete a Skill Association

Open `DELETE /api/users/{userId}/skills/{userSkillId}` and execute, using a `userSkillId` from the retrieved list.

Expected: `204 No Content`.

### 10. Verify Changes in PostgreSQL

```sql
SELECT * FROM user_skills;
SELECT * FROM skills;
```

---

## Current Project Status

### Implemented

- Spring Boot backend foundation (`BackendApplication.java`, `application.properties`).
- PostgreSQL database configuration (`skill-swap` database, `ddl-auto=update`).
- Entity and database modelling: `User`, `Skill`, `UserSkill`, `ExchangeOffer` entities with `SkillType` and `OfferStatus` enums.
- Authentication API: `POST /api/auth/register`, `POST /api/auth/login` with BCrypt hashing, duplicate email handling, and validation.
- Skill Management API: `POST /api/users/{userId}/skills`, `GET /api/users/{userId}/skills`, `DELETE /api/users/{userId}/skills/{userSkillId}`.
- Global exception handling for `400`, `401`, and `409` responses.
- Swagger UI / OpenAPI documentation.
- Frontend: React + Vite + Tailwind CSS application with mock data (UI only; no backend API integration).

### Planned / Not Yet Implemented

- User Discovery APIs (browse other users, search, filter).
- Exchange Offer APIs (create, accept, reject, view incoming/outgoing offers).
- Real-time chat (WebSocket dependency declared but not implemented).
- Frontend-to-backend integration (frontend currently uses hardcoded mock data).
- Session scheduling.
- Reviews and feedback.
- Password reset and email verification.
- JWT or session-based authentication (login currently performs stateless credential verification only).

---

## Future Scope

- Implement Exchange Offer controller and service.
- Implement User Discovery APIs.
- Connect the React frontend to the Spring Boot backend.
- Add real-time chat using Spring WebSocket.
- Add session scheduling and reviews.
- Add JWT-based authentication for secured API access.

---

## Contributors

Contributor details are not available in the repository. Add names and GitHub profile links here as appropriate.

- **Backend Engineer:** Rohan R (identified in source comments)
- Additional contributors: To be added

---
