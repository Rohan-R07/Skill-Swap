# SkillSwap

### A Peer-to-Peer Skill Exchange Platform

> Learn together. Teach what you know. Grow through exchange.

SkillSwap is a peer-to-peer skill exchange platform where users connect
with others, teach skills they already know, and learn skills they want
to develop. A user who can teach Java and wants to learn UI/UX design,
for example, can discover someone with complementary interests and
propose an exchange.

This is an **academic project and local prototype** focused on Java
Object-Oriented Programming, backend development, clean architecture,
and practical design patterns.

------------------------------------------------------------------------

## Table of Contents

-   [Features](#features)
-   [How It Works](#how-it-works)
-   [Technology Stack](#technology-stack)
-   [Architecture](#architecture)
-   [OOP and Design Patterns](#oop-and-design-patterns)
-   [Core Domain Model](#core-domain-model)
-   [Planned API](#planned-api)
-   [Project Structure](#project-structure)
-   [Getting Started](#getting-started)
-   [Team Workflow](#team-workflow)
-   [Roadmap](#roadmap)
-   [Scope](#scope)
-   [Contributors](#contributors)

## Features

### Core features

-   **Registration and login:** create an account and access a personal
    profile.
-   **User profiles:** manage basic profile information and learning
    interests.
-   **Skill management:** list skills a user can teach and skills they
    want to learn.
-   **User discovery:** browse and search for potential learning
    partners.
-   **Skill matching:** identify users with complementary teaching and
    learning interests.
-   **Exchange offers:** send, receive, accept, reject, or withdraw
    exchange requests.
-   **Exchange tracking:** view incoming, outgoing, and accepted
    exchanges.

### Additional features (time permitting)

-   **Real-time chat:** communicate with an exchange partner.
-   **Session scheduling:** arrange learning sessions.
-   **Reviews and feedback:** share feedback after an exchange.

## How It Works

1.  A user registers and creates a profile.
2.  The user adds skills they can teach and skills they want to learn.
3.  SkillSwap identifies users with complementary interests.
4.  The user views a potential match and sends an exchange offer.
5.  The recipient accepts or rejects the offer.
6.  Once accepted, both users coordinate their learning exchange.
7.  Optional chat, scheduling, and feedback features support the
    exchange.

## Technology Stack

Layer                     Technology           Responsibility
  ------------------------- -------------------- --------------------------------------
Frontend                  React                User interface
Frontend tooling          Vite                 Development server and build tooling
Language                  JavaScript           Frontend logic
Styling                   Tailwind CSS         UI styling
Routing                   React Router         Client-side navigation
HTTP client               Axios                Backend API communication
Backend                   Java + Spring Boot   Backend application
REST APIs                 Spring Web           HTTP endpoints
Persistence               Spring Data JPA      Repository abstraction
ORM                       Hibernate            Object-relational mapping
Database                  PostgreSQL           Persistent data storage
JDBC                      PostgreSQL Driver    Java/database connectivity
Real-time communication   Spring WebSocket     Live chat
Build tool                Maven                Dependency and build management
Linting                   ESLint               Frontend code-quality checks

The stack reflects the planned setup. Optional dependencies and features
will be added only when needed.

## Architecture

SkillSwap uses a **modular monolith with a layered backend**. The
frontend and backend are separate applications maintained in one
repository.

``` text
                  USER
                   |
                   v
          +-------------------+
          | React + Vite      |
          | Frontend          |
          +-------------------+
                   |
              HTTP / REST
                   |
                   v
          +-------------------+
          | Spring Boot       |
          | Backend           |
          +-------------------+
                   |
          +--------+---------+
          |        |         |
          v        v         v
      Controller Service  Repository
                              |
                              v
                         PostgreSQL
```

### Backend layers

-   **Controller:** receives HTTP requests and returns responses.
-   **Service:** contains business rules and application logic.
-   **Repository:** performs database operations through Spring Data
    JPA.
-   **Domain/model:** represents concepts such as users, skills, and
    offers.

The frontend communicates with the backend through REST APIs. Spring
WebSocket may support real-time chat.

## OOP and Design Patterns

OOP is a primary learning and assessment objective.

  -----------------------------------------------------------------------
Concept                             Application
  ----------------------------------- -----------------------------------
Encapsulation                       Control access to object data and
protect offer state transitions.

Abstraction                         Define interfaces for matching
strategies and replaceable
behaviours.

Inheritance                         Use only where a genuine shared
relationship exists.

Polymorphism                        Run different matching strategies
through a common interface.
  -----------------------------------------------------------------------

### Design patterns

-   **Strategy Pattern:** encapsulates different approaches to
    calculating skill compatibility.
-   **State Pattern:** models valid exchange-offer states such as
    pending, accepted, rejected, and withdrawn.
-   **Observer Pattern (optional):** can notify users when relevant
    events occur.
-   **Factory Method (optional):** may be used if different
    learning-session types are introduced.

Patterns will be used where they solve an actual design problem, not
simply to increase the pattern count.

## Core Domain Model

  -----------------------------------------------------------------------
Concept                             Responsibility
  ----------------------------------- -----------------------------------
`User`                              Represents a registered platform
member.

`Skill`                             Represents a skill that can be
taught or learned.

`UserSkill`                         Associates a user with a skill and
a teach/learn role.

`ExchangeOffer`                     Represents a proposed exchange and
its lifecycle status.

`LearningSession`                   Represents a planned learning
session.

`Message`                           Represents a chat message, if chat
is implemented.

`Review`                            Represents feedback after an
exchange, if reviews are
implemented.
  -----------------------------------------------------------------------

### Exchange offer lifecycle

``` text
                  +---------+
                  | PENDING |
                  +---------+
                   /   |   \
                  v    v    v
            +----------+ +----------+ +-----------+
            | ACCEPTED | | REJECTED | | WITHDRAWN |
            +----------+ +----------+ +-----------+
```

Only valid transitions should be allowed. For example, an already
rejected or withdrawn offer must not later be accepted.

## Planned API

These routes describe the **intended API contract**. They are not a
claim that every endpoint has already been implemented.

Method     Endpoint                    Purpose
  ---------- --------------------------- -----------------------------
`POST`     `/api/auth/register`        Register a user
`POST`     `/api/auth/login`           Log in
`GET`      `/api/users`                List users
`GET`      `/api/users/{id}`           Get a user profile
`PUT`      `/api/users/{id}`           Update a profile
`POST`     `/api/skills`               Add a skill
`GET`      `/api/skills`               List skills
`PUT`      `/api/skills/{id}`          Update a skill
`DELETE`   `/api/skills/{id}`          Delete a skill
`GET`      `/api/users/discover`       Discover potential partners
`GET`      `/api/matches`              Retrieve skill matches
`POST`     `/api/offers`               Create an exchange offer
`GET`      `/api/offers/incoming`      View received offers
`GET`      `/api/offers/outgoing`      View sent offers
`PUT`      `/api/offers/{id}/accept`   Accept an offer
`PUT`      `/api/offers/{id}/reject`   Reject an offer
`POST`     `/api/sessions`             Schedule a learning session
`GET`      `/api/sessions`             View learning sessions

Agree on request/response DTOs, validation, endpoint paths, and
ownership rules before frontend integration.

## Project Structure

``` text
Skill-Swap/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/skillswap/backend/
│   │   │   │   ├── controller/
│   │   │   │   ├── service/
│   │   │   │   ├── repository/
│   │   │   │   ├── model/
│   │   │   │   ├── dto/
│   │   │   │   ├── strategy/
│   │   │   │   └── config/
│   │   │   └── resources/application.properties
│   │   └── test/
│   ├── pom.xml
│   └── mvnw.cmd
├── frontend/
│   ├── public/
│   ├── src/
│   │   ├── components/
│   │   ├── pages/
│   │   ├── services/
│   │   ├── App.jsx
│   │   └── main.jsx
│   ├── package.json
│   └── vite.config.js
└── README.md
```

This is a suggested structure. Add packages and files as implementation
grows; avoid unnecessary empty layers.

## Getting Started

### Prerequisites

-   A JDK compatible with the configured Spring Boot version.
-   Node.js and npm.
-   PostgreSQL.
-   IntelliJ IDEA or another Java IDE.
-   Git.

### 1. Clone the repository

``` bash
git clone <repository-url>
cd Skill-Swap
```

### 2. Configure PostgreSQL

Create a local database named `skillswap`, then configure
`backend/src/main/resources/application.properties`:

``` properties
spring.datasource.url=jdbc:postgresql://localhost:5432/skillswap
spring.datasource.username=YOUR_DB_USERNAME
spring.datasource.password=YOUR_DB_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

Replace the example credentials with your local values. Do not commit
real passwords or secrets to Git.

### 3. Run the backend

Open a terminal in `backend`:

**Windows (Maven Wrapper):**

``` powershell
.\mvnw.cmd spring-boot:run
```

Or, if Maven is installed:

``` bash
mvn spring-boot:run
```

The backend is expected to run at `http://localhost:8080`.

### 4. Run the frontend

Open a separate terminal in `frontend`:

``` bash
npm install
npm run dev
```

Open the local URL printed by Vite, commonly `http://localhost:5173`.

Keep the frontend and backend terminals running during development.
PostgreSQL must also be running for database-backed features.

## Team Workflow

The project is developed by two contributors. A suggested responsibility
split:

Area                                  Contributor A         Contributor B
  ------------------------------------- --------------------- ---------------------
Authentication and profiles           Primary               Integration support
Skills and skill management           Primary               Integration support
User discovery                        Primary               Integration support
Matching engine and strategies        Integration support   Primary
Exchange offers and state lifecycle   Integration support   Primary
Learning sessions                     As agreed             Primary
Chat integration                      Shared / as agreed    Primary

### Git workflow

-   Keep `main` stable.
-   Create a feature branch for each task.
-   Commit focused changes with clear messages.
-   Open a Pull Request for review before merging.
-   Agree on shared entities, DTOs, and API contracts before parallel
    implementation.
-   Avoid editing the same files concurrently.

Example:

``` bash
git checkout -b feature/user-skill-management
git add .
git commit -m "Add user and skill management"
git push -u origin feature/user-skill-management
```

## Roadmap

-   [ ] Initialize the Spring Boot backend.
-   [ ] Initialize the React + Vite frontend.
-   [ ] Configure the local PostgreSQL database.
-   [ ] Agree on shared domain models and API contracts.
-   [ ] Implement registration and login.
-   [ ] Implement profiles and skill management.
-   [ ] Implement user discovery.
-   [ ] Implement matching strategies.
-   [ ] Implement exchange offers and state transitions.
-   [ ] Integrate the frontend with backend APIs.
-   [ ] Test the complete core exchange workflow.
-   [ ] Add real-time chat if time permits.
-   [ ] Add session scheduling and reviews if time permits.
-   [ ] Complete documentation and project demonstration.

## Scope

SkillSwap is intentionally scoped as a **local academic prototype**. The
priority is a working core workflow and understandable object-oriented
design.

The current scope does not require production deployment, microservices,
Docker/Kubernetes, distributed infrastructure, payment processing,
AI-based recommendations, or complex enterprise authentication.

## Learning Outcomes

This project provides practical experience with:

-   Object-Oriented Programming in Java.
-   SOLID principles and separation of responsibilities.
-   Design patterns applied to real use cases.
-   REST API design and frontend-backend integration.
-   Spring Boot, Spring Data JPA, and Hibernate.
-   Relational database modelling with PostgreSQL.
-   React component-based development.
-   Git branching, pull requests, and collaborative engineering.

## Contributors

Add contributor names and GitHub profile links:

-   **Contributor A:** Name --- GitHub profile
-   **Contributor B:** Name --- GitHub profile

------------------------------------------------------------------------

```{=html}
<p align="center">
```
`<strong>`{=html}Learn a skill. Share a skill. Build
together.`</strong>`{=html}
```{=html}
</p>
```
