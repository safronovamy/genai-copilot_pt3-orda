# Orders Management API

## Project Overview

The Orders Management API is a Spring Boot application designed to manage customer orders.
It provides RESTful endpoints for creating, retrieving, and filtering orders.
The application uses an H2 in-memory database to store order data, making it easy to set up and run locally.

## Tech Stack

- Java 17
- Spring Boot
- Spring Web (REST)
- Spring Data JPA
- JPA Specifications (for filtering)
- H2 in-memory database
- Maven
- Lombok
- Jackson
- JUnit 5 + Spring MockMvc

## Running the Application Locally

### Prerequisites

- Java 17
- Maven 3.8+

### Run the application

1. Navigate to the project directory:
   ```bash
   cd orders-management-api
   ```

2. Build the project:
   ```bash
   mvn clean install
   ```

3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

The application will start on:  
`http://localhost:8080`

## Database Setup

The application uses an H2 in-memory database for development and testing purposes.
The database is automatically configured when the application starts.

The `data.sql` file located in the `src/main/resources` directory contains SQL statements
to seed the database with **50 sample orders** upon application startup.

Each order includes the following fields:
- `id`
- `customerName`
- `amount`
- `status`
- `createdAt`

## REST Endpoints

### 1. Create a New Order

- **Endpoint:** `POST /orders`
- **Description:** Creates a new order.

**Request Body:**
```json
{
  "customerName": "John Doe",
  "amount": 150.75,
  "status": "NEW"
}
```

**Response:**  
Returns the created order with a generated `id` and `createdAt` timestamp.

**cURL Example:**
```bash
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "John Doe",
    "amount": 150.75,
    "status": "NEW"
  }'
```

### 2. Retrieve Orders with Pagination and Filtering

- **Endpoint:** `GET /orders`
- **Description:** Retrieves a paginated list of orders with optional filtering.

#### Query Parameters
- `page` (optional, default: `1`) — page number (**1-based**, minimum: 1)
- `limit` (optional, default: `10`, maximum: `100`) — items per page
- `status` (optional): `NEW`, `PAID`, `SHIPPED`, `CANCELLED`
- `minAmount` (optional): minimum order amount (inclusive)
- `maxAmount` (optional): maximum order amount (inclusive)
- `dateFrom` (optional, `YYYY-MM-DD`): filter orders created on or after this date
- `dateTo` (optional, `YYYY-MM-DD`): filter orders created on or before this date

#### Request Examples

**1. Default pagination**
```bash
curl -X GET "http://localhost:8080/orders"
```

**2. Pagination with status filter**
```bash
curl -X GET "http://localhost:8080/orders?page=2&limit=5&status=PAID"
```

**3. Combined filters (amount range + date range)**
```bash
curl -X GET "http://localhost:8080/orders?page=1&limit=10&minAmount=100&maxAmount=500&dateFrom=2025-12-01&dateTo=2025-12-31"
```

#### Sample Response
```json
{
  "items": [
    {
      "id": 1,
      "customerName": "John Doe",
      "amount": 150.75,
      "status": "PAID",
      "createdAt": "2025-12-15T10:30:00Z"
    },
    {
      "id": 2,
      "customerName": "Jane Smith",
      "amount": 299.99,
      "status": "NEW",
      "createdAt": "2025-12-16T14:22:00Z"
    }
  ],
  "page": 1,
  "limit": 10,
  "totalItems": 47,
  "totalPages": 5
}
```

#### Error Response Example (400 Bad Request)
```json
{
  "status": 400,
  "message": "Constraint violation",
  "details": {
    "violations": {
      "getOrders.limit": "must be less than or equal to 100"
    }
  }
}
```
