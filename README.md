# Pixel Shop

Spring Boot backend for Pixel Shop with email/password authentication, Google OAuth login, JWT access tokens, refresh-token cookies, PostgreSQL persistence, and Swagger API documentation.

## Tech Stack

- Java 17
- Spring Boot 3.5
- Spring Security
- Spring Data JPA
- PostgreSQL
- Maven Wrapper
- OpenAPI / Swagger UI

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/NhatDoo/Pixel_shop.git
cd Pixel_shop
```

### 2. Configure environment variables

Create a local `.env` file from the example:

```bash
cp .env.example .env
```

On Windows PowerShell:

```powershell
Copy-Item .env.example .env
```

Update `.env` with your local values:

```properties
DB_URL=jdbc:postgresql://localhost:5432/tmdt
DB_USERNAME=postgres
DB_PASSWORD=your-database-password

JWT_SECRET=your-long-random-jwt-secret-at-least-64-bytes

GOOGLE_OAUTH_CLIENT_ID=your-google-oauth-client-id.apps.googleusercontent.com

JPA_DDL_AUTO=update
JPA_SHOW_SQL=true
```

Important: `.env` is ignored by Git. Do not commit real passwords, JWT secrets, or OAuth credentials.

### 3. Prepare PostgreSQL

Create a PostgreSQL database matching your `DB_URL`. The default example expects:

```text
database: tmdt
host: localhost
port: 5432
```

### 4. Run the application

On Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

On macOS/Linux:

```bash
./mvnw spring-boot:run
```

The app starts on:

```text
http://localhost:8080
```

## API Documentation

Swagger UI is available after the app starts:

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

## Main Endpoints

| Method | Path | Description |
| --- | --- | --- |
| `POST` | `/auth/register` | Register a new user |
| `POST` | `/auth/login` | Login with email/password |
| `POST` | `/auth/google` | Login with Google ID token |
| `POST` | `/auth/refresh` | Refresh access token using refresh cookie |
| `POST` | `/auth/logout` | Logout current session |
| `GET` | `/api/users/me` | Get current authenticated user |

## Google Auth Test Page

A simple local test page is included:

```text
http://localhost:8080/google-auth-test.html
```

Enter your Google OAuth client ID from `.env`, sign in, exchange the Google ID token with `/auth/google`, then call `/api/users/me`.

## Running Tests

On Windows:

```powershell
.\mvnw.cmd test
```

On macOS/Linux:

```bash
./mvnw test
```

## Repository Safety

This repo tracks `.env.example` only. The real `.env`, build output, IDE files, and Maven `target/` folder are ignored.
