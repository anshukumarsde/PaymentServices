# PaymentServices

Prototype to build a payment processing service.

## What it does so far

This project is a minimal Spring Boot application that establishes the foundation for a payment service. At this stage, it provides:

- A Spring Boot app entry point for running the service
- A `Payment` domain model with:
  - unique payment ID
  - source account
  - destination account
  - amount
  - payment status (`PENDING`, `SETTLED`, `REJECTED`)
- REST endpoints for basic payment operations:
  - `GET /api/hello` to confirm the service is running
  - `POST /api/payments` to create a payment request
  - `GET /api/payments` to list saved payments
  - `GET /api/payments/{id}` to fetch a specific payment
  - `PATCH /api/payments/{id}/status` to mark a pending payment as settled or rejected
- PostgreSQL persistence for payment records through Spring Data JPA
- Validation to ensure source and destination accounts are provided and amounts are greater than zero
- Payment status can transition once from `PENDING` to `SETTLED` or `REJECTED`
- Plain HTTP for simple local development; no authentication is currently configured

## Current architecture

The project includes:

- `PaymentApplication` - starts the application
- `PaymentController` - exposes REST endpoints
- `PaymentService` - validates requests and manages payment state changes
- `PaymentRepository` - saves and retrieves payments through Spring Data JPA
- `Payment` - JPA entity for persisted payment records
- `PaymentRequest` - incoming payload for creating a payment
- `PaymentStatus` - enum representing payment lifecycle states

## How to run

The service listens over HTTP on port `8080` and requires a local PostgreSQL database. Docker Desktop must be running.

1. From the project root, start PostgreSQL:
   `docker compose up -d --wait`
2. Start the application:
   `mvn spring-boot:run`
3. Open `http://localhost:8080/api/hello` to check that the service started.

The local database defaults are database `payments`, username `payments`, and password `payments_local_dev`. These development-only values are for a database bound to localhost. Configure `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` to connect to a different database. Hibernate automatically creates or updates the schema for this prototype; use versioned schema migrations before production.

The PostgreSQL data is stored in a Docker volume and survives container restarts. Stop the database with `docker compose down`. Running `docker compose down -v` deletes the local database and all its payment records.

This unauthenticated HTTP setup is for local development only. Do not send real payment data or expose the service publicly; use HTTPS and proper authentication before deployment.

## Tests

Run the service unit tests and JPA repository tests with:

`mvn test`

## Run from IntelliJ IDEA

1. Open the project by selecting its `pom.xml` and import it as a Maven project.
2. Set the Project SDK to Java 17 and wait for Maven dependencies to finish importing.
3. Start PostgreSQL from the IDE terminal with `docker compose up -d --wait`.
4. Open `PaymentApplication.java` and click the run icon beside its `main` method.
5. Open `http://localhost:8080/api/hello` to check that the service started.

## Status

This is still an early prototype. Payment status changes are simulated and do not move funds. Payments are persisted locally but are not sent to an external payment provider.
