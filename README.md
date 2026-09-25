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

## Test the API from PowerShell

| Method | Endpoint | Purpose |
|---|---|---|
| `POST` | `/api/accounts` | Create an account with its account number, holder name, and opening balance |
| `GET` | `/api/accounts` | List all accounts and balances |
| `GET` | `/api/accounts/{accountNumber}` | View one account |
| `PUT` | `/api/accounts/{accountNumber}` | Update the account holder name |
| `DELETE` | `/api/accounts/{accountNumber}` | Delete an account only if its balance is zero and it has no transfer history |
| `POST` | `/api/transfers` | Transfer funds and receive a receipt |

Follow these steps in order. Use one PowerShell window for the commands and keep the application running in IntelliJ or in a separate terminal. The app seeds accounts `1001` ($1,000), `1002` ($500), and `1003` ($250) each time it starts. Amounts below are USD.

### 1. Start the application

In IntelliJ, open `PaymentApplication.java` and click **Run** next to `main`. Alternatively, from the project folder run:

```powershell
mvn spring-boot:run
```

Wait until the startup log says the application has started. Keep this terminal open. Open a second PowerShell window for the API calls below.

### 2. List the seeded accounts

```powershell
Invoke-RestMethod "http://localhost:8080/api/accounts"
```

You should see accounts `1001`, `1002`, and `1003` with their holder names and balances.

### 3. Create an account

```powershell
Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/accounts" -ContentType "application/json" -Body '{"accountNumber":"2001","accountHolderName":"Casey Doe","openingBalance":0.00}'
```

The response should show account `2001`, holder `Casey Doe`, and balance `0.00`. Account numbers must be unique; if you repeat this request without restarting the app, it returns a conflict because `2001` already exists.

### 4. Read the new account

```powershell
Invoke-RestMethod "http://localhost:8080/api/accounts/2001"
```

The response should show the account you just created.

### 5. Update the account holder name

```powershell
Invoke-RestMethod -Method Put -Uri "http://localhost:8080/api/accounts/2001" -ContentType "application/json" -Body '{"accountHolderName":"Casey Jones"}'
```

The response should show `Casey Jones`. A `PUT` request changes the holder name only; it cannot change the account number or balance.

### 6. Transfer money between the seeded accounts

```powershell
Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/transfers" -ContentType "application/json" -Body '{"fromAccountNumber":"1001","toAccountNumber":"1002","amount":25.00}'
```

The response is the transfer receipt, with a `transferId`, amount `$25.00`, account numbers `1001` and `1002`, status `COMPLETED`, and a timestamp.

### 7. Confirm the transfer changed balances

```powershell
Invoke-RestMethod "http://localhost:8080/api/accounts"
```

Account `1001` should now have `$975.00`; account `1002` should have `$525.00`. Account `2001` remains at zero.

### 8. Check that unsafe deletes are rejected

An account with a non-zero balance cannot be deleted:

```powershell
Invoke-RestMethod -Method Delete -Uri "http://localhost:8080/api/accounts/1001"
```

This request should return HTTP `409 Conflict`. An account with transfer history is also protected; for example, account `1002` cannot be deleted after the transfer in step 6:

```powershell
Invoke-RestMethod -Method Delete -Uri "http://localhost:8080/api/accounts/1002"
```

This should also return HTTP `409 Conflict`. PowerShell displays an error for these expected responses.

### 9. Delete the unused zero-balance account

```powershell
Invoke-RestMethod -Method Delete -Uri "http://localhost:8080/api/accounts/2001"
```

The response is empty because a successful delete returns HTTP `204 No Content`. Confirm it was deleted by listing accounts:

```powershell
Invoke-RestMethod "http://localhost:8080/api/accounts"
```

The H2 console is available at `http://localhost:8080/h2-console` while the app is running. Connect with JDBC URL `jdbc:h2:mem:payments`, username `sa`, and a blank password. This is an in-memory database: stopping the application clears data and the three demo accounts are seeded again on the next start. Use H2, not SQLite.

## Database tables and relationship

The `accounts` table stores `account_number` (primary key), `account_holder_name`, `balance`, and `created_at`. The `transfers` table stores `transfer_id` (primary key), `from_account_number`, `to_account_number`, `amount`, `status`, and `created_at`.

Both account-number columns in `transfers` are foreign keys to `accounts.account_number`. This models a transfer's source and destination accounts directly in the database, so a transfer cannot reference a non-existent account. Transfer history also prevents its referenced accounts from being deleted.

To inspect the schema and related rows, open the H2 console at `http://localhost:8080/h2-console`, connect with the settings above, and run:

```sql
SHOW TABLES;
SELECT * FROM accounts;
SELECT * FROM transfers;
SELECT t.transfer_id, src.account_number AS from_account, dst.account_number AS to_account,
       t.amount, t.status, t.created_at
FROM transfers t
JOIN accounts src ON src.account_number = t.from_account_number
JOIN accounts dst ON dst.account_number = t.to_account_number;
```

## Tests

Run all tests with `mvn test`. Tests cover the API, transfer rules, and H2 repositories.

## Summary

PaymentServices is a small, local banking MVP for demonstrating basic account management and internal transfers. It lets a user create, view, update, and safely delete demo accounts, then transfer funds between accounts while validating balances and recording each transfer with database links to both accounts. It is intended for learning, interview demonstrations, and API practice—not for production banking or moving real money.
