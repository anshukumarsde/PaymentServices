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
- REST endpoints for basic payment operations:
  - `GET /api/hello` to confirm the service is running
  - `POST /api/payments` to create a payment request
  - `GET /api/payments` to list saved payments
  - `GET /api/payments/{id}` to fetch a specific payment
- In-memory H2 database for payment records, accessed through a repository and service
- Validation to ensure source and destination accounts are provided and amounts are greater than zero
- Plain HTTP for simple local development; no authentication is currently configured

## Current architecture

The project includes:

- `PaymentApplication` - starts the application
- `PaymentController` - exposes the API
- `PaymentService` - creates and retrieves payments
- `PaymentRepository` - persists payments with Spring Data JPA
- `Payment` - JPA entity for payment records
- `PaymentRequest` - incoming payload for creating a payment

## How to run

The service listens over HTTP on port `8080` and uses an in-memory H2 database. No database setup is needed.

1. Start the application:
   `mvn spring-boot:run`
2. Open `http://localhost:8080/api/hello` to check that the service started.

Payments are stored in H2 and cleared when the app stops. The simple controller-service-repository structure demonstrates a typical Spring application while keeping setup minimal.

## Try the APIs with PowerShell

With the application running, open PowerShell and run each command on one line:

```powershell
# Check that the service is running
Invoke-RestMethod "http://localhost:8080/api/hello"

# Create a payment and save its response (including the generated ID)
$payment = Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/payments" -ContentType "application/json" -Body '{"sourceAccount":"ACC-100","destinationAccount":"ACC-200","amount":150.50}'

# List all payments
Invoke-RestMethod "http://localhost:8080/api/payments"

# Fetch the payment created above
Invoke-RestMethod "http://localhost:8080/api/payments/$($payment.id)"
```

The POST command returns the new payment and saves it in `$payment`. Use that same PowerShell session for the fetch command so `$payment.id` is available.

To inspect the running database, open `http://localhost:8080/h2-console` and connect with JDBC URL `jdbc:h2:mem:payments`, username `sa`, and a blank password. Do not configure this H2 connection as SQLite.

This unauthenticated HTTP setup is for local development only. Do not send real payment data or expose the service publicly; use HTTPS and proper authentication before deployment.

## Tests

Run the tests with:

`mvn test`

## Run from IntelliJ IDEA

1. Open the project by selecting its `pom.xml` and import it as a Maven project.
2. Set the Project SDK to Java 17 and wait for Maven dependencies to finish importing.
3. Open `PaymentApplication.java` and click the run icon beside its `main` method.
4. Open `http://localhost:8080/api/hello` to check that the service started.

## Status

This is a minimal MVP for creating and retrieving payment records. It does not move funds or connect to an external payment provider.
