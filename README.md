# Personal Wallet API

A RESTful API to track personal income and expenses, built with **Spring Boot 3**, **Java 17**, and **MongoDB**.

---

## Stack

| Layer       | Technology                  |
|-------------|-----------------------------|
| Language    | Java 17                     |
| Framework   | Spring Boot 3               |
| Database    | MongoDB                     |
| Build Tool  | Maven                       |

---

## Run the project

### Prerequisites
- Java 17+
- Maven 3.8+
- MongoDB running locally on port `27017`

### Start

```bash
mvn spring-boot:run
```

The API will be available at `http://localhost:8080/api`.

---

## Endpoints

### `POST /api/transactions`
Add a new transaction.

**Request body:**
```json
{
  "amount": 50.00,
  "category": "Cibo",
  "description": "Cena al ristorante",
  "type": "EXPENSE"
}
```

`type` can be `EXPENSE` or `INCOME`.

**Response:** `201 Created` with the saved transaction.

---

### `GET /api/transactions`
Get all transactions.

**Response:** `200 OK`
```json
[
  {
    "id": "abc123",
    "amount": 50.00,
    "category": "Cibo",
    "description": "Cena al ristorante",
    "date": "2024-01-15T20:30:00",
    "type": "EXPENSE"
  }
]
```

---

### `GET /api/balance`
Get the current wallet balance (`INCOME - EXPENSE`).

**Response:** `200 OK`
```json
{
  "balance": 450.00
}
```

---

## Project structure

```
src/main/java/com/wallet/api/
├── PersonalWalletApiApplication.java   # Entry point
├── model/
│   └── Transaction.java                # MongoDB document
├── repository/
│   └── TransactionRepository.java      # Data access layer
├── service/
│   └── TransactionService.java         # Business logic
└── controller/
    └── TransactionController.java      # REST endpoints
```
