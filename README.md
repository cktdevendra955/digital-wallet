# Digital Wallet System

A REST API backend for a digital wallet — register/login with JWT, top up your wallet,
transfer money to other users, and view transaction history. Built with Spring Boot,
MySQL, Spring Security (JWT), and documented with Swagger/OpenAPI.

## Tech Stack
- Java 17, Spring Boot 3.2.5
- Spring Web, Spring Data JPA, Spring Security
- MySQL
- JWT (jjwt)
- Swagger / springdoc-openapi
- Lombok

## Project Structure (feature-based)
```
com.wallet
 ├── common/        -> config, security (JWT), global exception handling, response wrappers
 ├── user/           -> registration, login
 ├── wallet/         -> balance, add money
 └── transaction/    -> transfer money, transaction history
```

## Setup

### 1. Create the database
MySQL will auto-create the schema on first run (`createDatabaseIfNotExist=true`), but make
sure MySQL itself is running and update credentials in
`src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/wallet_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=root
```

### 2. Run the app
```bash
mvn clean install
mvn spring-boot:run
```

App starts on **http://localhost:8080**

### 3. Open Swagger UI
```
http://localhost:8080/swagger-ui.html
```
All endpoints are documented there and you can try them directly (click "Authorize" and
paste your JWT token after logging in).

## API Flow (test in this order)

### 1. Register
```
POST /api/auth/register
{
  "name": "Alice",
  "email": "alice@example.com",
  "password": "password123"
}
```
Response includes a JWT token and creates an empty wallet automatically.

### 2. Login
```
POST /api/auth/login
{
  "email": "alice@example.com",
  "password": "password123"
}
```
Copy the `token` from the response. For every request below, add header:
```
Authorization: Bearer <token>
```

### 3. Check wallet balance
```
GET /api/wallet/balance
```

### 4. Add money
```
POST /api/wallet/add-money
{
  "amount": 500
}
```

### 5. Register a second user (e.g. bob@example.com) and transfer money
```
POST /api/transactions/transfer
{
  "receiverEmail": "bob@example.com",
  "amount": 100,
  "description": "Lunch split"
}
```

### 6. View transaction history (paginated)
```
GET /api/transactions/history?page=0&size=10
```

## Notes on design decisions (useful for interviews)
- **Feature-based packages** instead of layer-based — each feature (`user`, `wallet`,
  `transaction`) is self-contained, making it easier to explain and extend.
- **Transaction feature depends on Wallet's service layer**, not its repository directly —
  keeps balance-mutation logic in one place.
- **`@Transactional` + pessimistic row lock** on the sender's wallet during a transfer,
  so two simultaneous transfer requests from the same account can't both read a stale
  balance and overdraw it.
- **Debit-then-credit ordering**: if debit fails (insufficient balance), the exception
  rolls back the whole transaction — no half-completed transfer is ever persisted.
- **Global exception handler** returns consistent JSON error responses across the app
  instead of raw stack traces.
- **JWT is stateless** — no server-side session, `SessionCreationPolicy.STATELESS`.

## Possible future improvements (good to mention as "next steps")
- Add refresh tokens
- Add role-based admin endpoints (view all users, freeze wallet)
- Rate limiting on transfer endpoint
- Dockerize with docker-compose (app + MySQL)
