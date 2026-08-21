# User Service

First microservice in the e-commerce platform. Handles registration, login (JWT), and user profile lookup. Runs on **port 8081** with its own PostgreSQL database (`user_service_db`) — no other service should ever touch this database directly.

## Run locally (without Docker)

1. Make sure PostgreSQL is running and create the database:
   ```sql
   CREATE DATABASE user_service_db;
   ```
2. Update `src/main/resources/application.yml` if your Postgres username/password differ from `postgres/postgres`.
3. From the project root:
   ```bash
   mvn spring-boot:run
   ```
   The service starts on `http://localhost:8081`.

## Run with Docker Compose (recommended — spins up Postgres too)

```bash
docker compose up --build
```

This starts both the `user-db` (Postgres) and `user-service` containers, wired together.

## API Endpoints

### Register
```
POST /api/auth/register
Content-Type: application/json

{
  "fullName": "Jane Doe",
  "email": "jane@example.com",
  "password": "strongpassword123"
}
```
Returns a JWT token + user info. Password is BCrypt-hashed before storage — never stored in plaintext.

### Login
```
POST /api/auth/login
Content-Type: application/json

{
  "email": "jane@example.com",
  "password": "strongpassword123"
}
```
Returns a JWT token on success.

### Get current user
```
GET /api/users/me
Authorization: Bearer <token>
```

### Get user by ID (for internal service-to-service calls)
```
GET /api/users/{id}
```
This endpoint is what your future **Order Service** will call to verify a user exists before creating an order — e.g. `GET http://user-service:8081/api/users/42`.

## Design notes

- **Password security**: BCrypt via Spring Security's `PasswordEncoder`. Never log or return the password field.
- **JWT secret**: The one in `application.yml` is a placeholder — replace it with a real random secret (min 256-bit for HS256) via an environment variable before this touches anything real.
- **Stateless auth**: No server-side sessions (`SessionCreationPolicy.STATELESS`) — this is required for microservices since you can't guarantee the next request hits the same instance.
- **Own database**: This is a core microservices principle — each service owns its data exclusively. When you build Product Service next, it gets its **own** separate Postgres database, not a shared one.

## Next step

Once this is running and you can register/login successfully, we build **Product Service** next — a second, independent Spring Boot project on its own port (e.g. 8082) with its own database, and you'll make Product Service call this User Service over REST to see real service-to-service communication in action.

Test it with curl:
```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"fullName":"Jane Doe","email":"jane@example.com","password":"strongpassword123"}'
```
