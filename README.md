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
- In-memory storage for prototype payment records
- Validation to ensure source and destination accounts are provided and amounts are greater than zero
- Payment status can transition once from `PENDING` to `SETTLED` or `REJECTED`
- Plain HTTP for simple local development; no authentication is currently configured

## Current architecture

The project includes:

- `PaymentApplication` - starts the application
- `PaymentController` - exposes REST endpoints
- `PaymentService` - handles in-memory creation and retrieval of payments
- `Payment` - payment data model
- `PaymentRequest` - incoming payload for creating a payment
- `PaymentStatus` - enum representing payment lifecycle states

## How to run

The service listens over HTTP on port `8080`; no keystore or credentials are required.

1. Start the app with Maven:
   `mvn spring-boot:run`
2. Open `http://localhost:8080/api/hello` to check that the service started.

This unauthenticated HTTP setup is for local development only. Do not send real payment data or expose the service publicly; use HTTPS and proper authentication before deployment.

## Tests

Run the payment service unit tests with:

`mvn test`

## Run from IntelliJ IDEA

1. Open the project by selecting its `pom.xml` and import it as a Maven project.
2. Set the Project SDK to Java 17 and wait for Maven dependencies to finish importing.
3. Select the `PaymentApplication` run configuration and click **Run**.
4. Open `http://localhost:8080/api/hello` to check that the service started.

If the run configuration does not appear, open `PaymentApplication.java` and click the run icon beside its `main` method.

## Status

This is still an early prototype. Payment status changes are simulated and do not move funds. The service does not yet connect to a database or external payment provider.
