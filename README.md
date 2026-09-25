# PaymentServices

Prototype to build a payment processing service.

## What it does so far

This project is a minimal Spring Boot application that establishes the foundation for a payment service. At this stage, it provides:

- A Spring Boot application entry point for running the service
- A `Payment` domain model with:
  - source account
  - destination account
  - amount
  - payment status (`PENDING`, `SETTLED`, `REJECTED`)
- An HTTP endpoint at `/api/hello` that confirms the service is running
- Basic security configuration using Spring Security with an in-memory user
- A simple starting structure that can be extended into real payment workflows

## Current architecture

The project includes:

- `PaymentApplication` - starts the application
- `PaymentController` - exposes REST endpoints
- `Payment` - payment data model
- `PaymentStatus` - enum representing payment lifecycle states
- `SecurityConfig` - configures authentication and basic HTTP security

## Status

This is still an early prototype and does not yet support real payment processing, database persistence, transaction validation, or external payment-provider integration.
