# PaymentServices

A small Spring Boot MVP that simulates internal transfers between demo bank accounts.

## What it does

- Creates, lists, views, updates the holder name, and safely deletes accounts
- Transfers funds between two accounts in one database transaction
- Rejects invalid requests, unknown account numbers, transfers to the same account, and insufficient funds
- Saves a transfer receipt in the in-memory H2 database and returns it in the transfer response

Account balances are changed only by transfers, not by account updates. An account can be deleted only when its balance is zero and it has no transfer history. Transfers cannot be edited or deleted because they are the transaction record. This does not connect to a real bank or move real money. Data resets when the app stops. Authentication and HTTPS are not configured; use only for local demos.

## Run

Use Java 17 and start `PaymentApplication` from IntelliJ, or run `mvn spring-boot:run`. The app starts on `http://localhost:8080`; no database installation is needed.

## Try it with PowerShell

Account endpoints provide simple CRUD. Transfer endpoints handle the actual money movement:

| Method | Endpoint | Purpose |
|---|---|---|
| `POST` | `/api/accounts` | Create an account with its account number, holder name, and opening balance |
| `GET` | `/api/accounts` | List all accounts and balances |
| `GET` | `/api/accounts/{accountNumber}` | View one account |
| `PUT` | `/api/accounts/{accountNumber}` | Update the account holder name |
| `DELETE` | `/api/accounts/{accountNumber}` | Delete an account only if its balance is zero and it has no transfer history |
| `POST` | `/api/transfers` | Transfer funds and receive a receipt |

Run these one-line commands in PowerShell while the app is running. Demo balances and transfers are in USD. The seeded accounts are `1001` ($1,000), `1002` ($500), and `1003` ($250).

```powershell
# Create a zero-balance account
Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/accounts" -ContentType "application/json" -Body '{"accountNumber":"2001","accountHolderName":"Casey Doe","openingBalance":0.00}'

# List accounts, or fetch one account
Invoke-RestMethod "http://localhost:8080/api/accounts"
Invoke-RestMethod "http://localhost:8080/api/accounts/1001"

# Update the holder name (account balance is not editable here)
Invoke-RestMethod -Method Put -Uri "http://localhost:8080/api/accounts/2001" -ContentType "application/json" -Body '{"accountHolderName":"Casey Jones"}'

# Transfer $25.00 from account 1001 to account 1002; the response is your receipt
Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/transfers" -ContentType "application/json" -Body '{"fromAccountNumber":"1001","toAccountNumber":"1002","amount":25.00}'

# Check the updated balances
Invoke-RestMethod "http://localhost:8080/api/accounts"

# Delete the unused zero-balance account
Invoke-RestMethod -Method Delete -Uri "http://localhost:8080/api/accounts/2001"
```

The transfer response contains a `transferId`, the from/to account numbers, amount, status, and timestamp. There is no separate transfer-history endpoint in this small MVP; each transfer is still recorded in H2.

The H2 console is available at `http://localhost:8080/h2-console` while the app is running. Connect with JDBC URL `jdbc:h2:mem:payments`, username `sa`, and a blank password. Use H2, not SQLite.

## Tests

Run all tests with `mvn test`. Tests cover the API, transfer rules, and H2 repositories.
