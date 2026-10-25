# Student Management Project

[![CI](https://github.com/Abhishek360007/Student_Manangement_Project/actions/workflows/ci.yml/badge.svg)](https://github.com/Abhishek360007/Student_Manangement_Project/actions/workflows/ci.yml)
[![Deploy to Render](https://render.com/images/deploy-to-render-button.svg)](https://render.com/deploy?repo=https://github.com/Abhishek360007/Student_Manangement_Project)

A REST API for managing students, their departments and their projects, built with **Spring Boot 3**, **Spring Data JPA**, **Spring Security** and an in-memory **H2** database. It includes interactive API docs (Swagger UI).

## Features

- Add, update (partial), and delete students
- Look up students by ID, email, index number, or a range of birth dates
- Search students by first or last name (case-insensitive, partial match)
- Filter students by department
- Statistics endpoint: total students, on budget vs. self-financed, and count per department
- Projects linked to students. Deleting a student also deletes their projects
- Request validation with readable error messages
- Consistent JSON error responses (`timestamp`, `status`, `message`, `path`)
- HTTP Basic authentication
- Swagger UI and H2 web console
- Unit, controller, and end-to-end tests

## Tech Stack

| Layer      | Technology                       |
|------------|----------------------------------|
| Language   | Java 17+                         |
| Framework  | Spring Boot 3.4                  |
| Persistence| Spring Data JPA / Hibernate, H2  |
| Security   | Spring Security (HTTP Basic)     |
| API docs   | springdoc-openapi (Swagger UI)   |
| Testing    | JUnit 5, Mockito, MockMvc        |
| Build      | Maven                            |

## Project Structure

```
src/main/java/com/abhishek/studentms
├── config/        # Message source, validator, OpenAPI setup
├── controller/    # REST endpoints (students, projects)
├── exception/     # Custom exceptions + global error handler
├── model/         # JPA entities and DTOs
├── repository/    # Spring Data repositories
├── security/      # Security filter chain
└── service/       # Business logic
src/main/resources
├── application.properties
├── db/schema.sql  # Table definitions
├── db/data.sql    # Sample data loaded at startup
└── langs/messages.properties  # Validation messages
```

## Getting Started

**Requirements:** JDK 17 or newer. Maven is optional because the wrapper is included.

```bash
git clone https://github.com/Abhishek360007/Student_Manangement_Project.git
cd Student_Manangement_Project

# run the app
./mvnw spring-boot:run        # Windows: mvnw.cmd spring-boot:run

# run the tests
./mvnw test
```

The app starts on **http://localhost:8080**.

| URL | Description |
|-----|-------------|
| http://localhost:8080/swagger-ui.html | Interactive API docs |
| http://localhost:8080/h2-console | DB console (JDBC URL `jdbc:h2:mem:studentdb`, user `sa`, empty password) |

### Credentials

All `/api/**` endpoints use HTTP Basic auth. The default login is `admin` / `admin123`. You can change it with environment variables:

```bash
APP_USERNAME=myuser APP_PASSWORD=secret ./mvnw spring-boot:run
```

## Deployment

The app ships with a `Dockerfile` and a Render blueprint (`render.yaml`), so it runs on any Docker host.

### Render (free)

1. Click the **Deploy to Render** button above, or go to Render → **New** → **Blueprint** and pick this repo.
2. Render builds the Docker image and starts the service. It checks `/actuator/health` to confirm the app is up.
3. A random `APP_PASSWORD` is generated for you. Find it in the service's **Environment** tab. The username is `admin`.
4. Open `https://<your-service>.onrender.com`. It redirects to Swagger UI.

Every push to `main` redeploys automatically.

> Free Render services sleep after about 15 minutes without traffic, so the first request after that takes around a minute. The database is in-memory, so data resets to the sample data on every restart or deploy.

### Docker (any host)

```bash
docker build -t student-management .
docker run -p 8080:8080 -e APP_PASSWORD=change-me student-management
```

| Environment variable | Default | Purpose |
|----------------------|---------|---------|
| `PORT` | `8080` | HTTP port (set automatically by Render, Railway, etc.) |
| `APP_USERNAME` | `admin` | API username |
| `APP_PASSWORD` | `admin123` | API password: **always change this in production** |
| `JAVA_OPTS` | `-XX:MaxRAMPercentage=75 -XX:+UseSerialGC` | JVM flags |

## API Reference

Base URL: `http://localhost:8080/api/v1`

### Students

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST   | `/students` | Add a student |
| GET    | `/students?department=Civil` | List all students (department filter is optional) |
| GET    | `/students/{id}` | Find by ID |
| GET    | `/students/email/{email}` | Find by email |
| GET    | `/students/index/{indexNumber}` | Find by index number |
| GET    | `/students/search?name=sha` | Search by first or last name |
| GET    | `/students/date-of-birth?from=2001-01-01&to=2002-12-31` | Students born between two dates |
| GET    | `/students/stats` | Student statistics |
| PUT    | `/students/{id}` | Update only the fields you send |
| DELETE | `/students/{id}` | Delete by ID |
| DELETE | `/students/email/{email}` | Delete by email |
| POST   | `/students/{id}/projects` | Add a project to a student |
| GET    | `/students/{id}/projects` | List a student's projects |
| GET    | `/students/{id}/projects/{projectId}` | Get one of a student's projects |

### Projects

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST   | `/projects` | Create a project with no student assigned |
| GET    | `/projects` | List all projects |
| GET    | `/projects/{id}` | Find by ID |
| PUT    | `/projects/{id}` | Update name and/or description |
| DELETE | `/projects/{id}` | Delete by ID |

### Example: create a student

```bash
curl -u admin:admin123 -X POST http://localhost:8080/api/v1/students \
  -H "Content-Type: application/json" \
  -d '{
        "firstName": "Kabir",
        "lastName": "Mehta",
        "dateOfBirth": "2003-07-19",
        "email": "kabir.mehta@example.com",
        "indexNumber": 2001,
        "department": "Physics",
        "isOnBudget": false
      }'
```

### Example error response

```json
{
  "timestamp": "2026-10-26T10:15:30.123",
  "status": 409,
  "message": "Email kabir.mehta@example.com is already in use.",
  "path": "/api/v1/students"
}
```

## Author

**Abhishek Raj** · [GitHub](https://github.com/Abhishek360007)

## Acknowledgements

The original idea and starting code came from [crni99/Student-Management-System](https://github.com/crni99/Student-Management-System).
