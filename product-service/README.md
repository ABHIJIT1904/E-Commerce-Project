# Product Service

Second microservice in the e-commerce platform. Handles the product catalog. Runs on **port 8082** with its own PostgreSQL database (`product_service_db`) — completely separate from User Service's database.

## The important part: this service calls User Service over REST

When you create a product, `ProductService.createProduct()` calls `UserServiceClient.userExists()`, which makes a real HTTP `GET` request to `http://<user-service>/api/users/{id}` to confirm the `createdByUserId` is a real user before allowing the product to be created.

This is the actual microservices moment: two independently deployable services, each with their own database, coordinating over the network. Things to notice in `UserServiceClient.java`:

- If User Service responds with 404 → `InvalidUserException` (400 to the client — "that user doesn't exist")
- If User Service can't be reached at all (down, timeout) → `UserServiceUnavailableException` (503 to the client — "try again later")

These are two **very different** failure modes that don't exist in a monolith, where a "user doesn't exist" check is just a database query in the same transaction. Here it's a network call that can fail in its own ways.

## Run everything together (recommended)

From the **parent folder** (`ecommerce-platform/`, one level up from this folder):
```bash
docker compose up --build
```
This starts 4 containers: `user-db`, `user-service`, `product-db`, `product-service` — all wired together. Product Service reaches User Service via the Docker network at `http://user-service:8081`.

## Run locally without Docker

1. Create the database: `CREATE DATABASE product_service_db;`
2. Make sure User Service is already running on port 8081 (Product Service needs it to validate users).
3. From this folder: `mvn spring-boot:run`

## Try it out

1. Register a user via User Service (port 8081) and note the returned `userId`.
2. Create a product using that ID:
```bash
curl -X POST http://localhost:8082/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Wireless Mouse",
    "description": "Ergonomic wireless mouse",
    "price": 25.99,
    "category": "Electronics",
    "stockQuantity": 100,
    "createdByUserId": 1
  }'
```
3. Now try it with a **fake** user ID (e.g. `999999`) — you should get a `400` with an `InvalidUserException` message, proving the cross-service check actually works.
4. Stop the User Service container/process and try creating a product again — you should get a `503 Service Unavailable` instead, proving the "dependency is down" path also works.

## Other endpoints

```
GET /api/products              # list all
GET /api/products?category=Electronics
GET /api/products?search=mouse
GET /api/products/{id}
DELETE /api/products/{id}
```

## What's deliberately missing (next steps in the roadmap)

- **No retries / circuit breaker yet.** Right now if User Service is briefly slow, the request just fails. Resilience4j comes in a later step of the roadmap — that's when you'll wrap `UserServiceClient` calls with a circuit breaker so repeated failures short-circuit instead of hammering a struggling service.
- **No service discovery.** The User Service URL is a hardcoded config value (`user-service.base-url`). Once Eureka is introduced, this becomes a service-name lookup instead.
- **No API Gateway / centralized auth.** `createdByUserId` is currently passed in the request body, which is not how you'd do it in a real system — normally the gateway would extract this from a validated JWT. That's also a later step.

These aren't oversights — they're intentionally left out so each concept gets introduced one at a time, per the roadmap.
