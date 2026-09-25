# PaymentServices

A small Spring Boot MVP that simulates internal transfers between demo bank accounts.

## What it does

- Lists seeded demo accounts and their balances
- Transfers funds between accounts in one database transaction
- Rejects invalid requests, unknown account numbers, transfers to the same account, and insufficient funds
- Records completed transfers in an in-memory H2 database

It does not connect to a real bank or move real money. Data resets when the app stops. Authentication and HTTPS are not configured; use only for local demos.

## Run

Use Java 17 and start `PaymentApplication` from IntelliJ, or run `mvn spring-boot:run`. The app starts on `http://localhost:8080`; no database installation is needed.

## API examples (PowerShell)

Run with the app started. Each command below is a single line. Demo balances and transfers are treated as USD. The seeded accounts are `1001` ($1,000), `1002` ($500), and `1003` ($250); use the same PowerShell session so `$transfer.transferId` remains available.

| Method | Endpoint | Purpose |
|---|---|---|
| `GET` | `/api/health` | Check that the API is running |
| `GET` | `/api/accounts` | List demo account balances |
| `POST` | `/api/transfers` | Move funds between two demo accounts |
| `GET` | `/api/transfers` | List recorded transfers |
| `GET` | `/api/transfers/{transferId}` | Get one transfer by its ID |

```powershell
# Check API health
Invoke-RestMethod "http://localhost:8080/api/health"

# View account numbers and balances
Invoke-RestMethod "http://localhost:8080/api/accounts"

# Transfer $25.00 from account 1001 to account 1002
$transfer = Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/transfers" -ContentType "application/json" -Body '{"fromAccountNumber":"1001","toAccountNumber":"1002","amount":25.00}'

# View the updated account balances
Invoke-RestMethod "http://localhost:8080/api/accounts"

# List completed transfers
Invoke-RestMethod "http://localhost:8080/api/transfers"

# Fetch the transfer created above
Invoke-RestMethod "http://localhost:8080/api/transfers/$($transfer.transferId)"
```

The H2 console is available at `http://localhost:8080/h2-console` while the app is running. Connect with JDBC URL `jdbc:h2:mem:payments`, username `sa`, and a blank password. Use H2, not SQLite.

## Tests

Run all tests with `mvn test`. Tests cover the API, transfer rules, and H2 repositories.
