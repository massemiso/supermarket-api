# Supermarket Management API
A REST API built with Spring Boot to manage supermarkets branches, products
and sales. This project was developed inspired by a technical test to
demonstrate skills in Java and Spring Boot.

## Features
- **CRUD** operations for Branches, Products and Sales.
- **Business Statistics** for best-selling products via native SQL projections.
- **Soft deletion** of entities: to preserve data integrity and allow for data recovery.
- Pagination and search sorting for collections.
- Consistent API responses with **JSON**, including exception details.
- **Optimistic Locking** using JPA @Version to prevent data overwriting in high-load scenarios.
- Logging with **SLF4J** and **Logback**.
- Automated auditing using **Spring Data JPA Auditing**.
- Dockerized application for easy deployment using **Docker**.

## Stack
- Java 21+
- Maven
- Spring Boot 3.x
- Spring Boot JPA (Hibernate)
- PostgreSQL (production/docker) 
- H2 (local development/in-memory)
- Testcontainers
- REST Assured
- Docker & Docker Compose
- Lombok & MapStruct (conceptually)

## Project Structure
~~~
src/main/java/com/massemiso/supermarket_api/
├── config/        # JPA Auditing, Web & Pagination settings
├── controller/    # REST Endpoints with @Valid 
├── dto/           # Records for immutable data transfer & Mappers
├── entity/        # JPA Models with @MappedSuperclass for Soft Delete
├── exception/     # Custom exceptions & @ControllerAdvice handler
├── repository/    # Data Access Layer (Native Queries & Projections)
├── service/       # Business Logic with @Transactional boundaries
└── SupermarketApplication.java      # Main application class
~~~

## Testing Strategy
This project follows a testing pyramid:
- **Unit Tests**: Using JUnit 5 and Mockito to test service logic and mappers in isolation. 
- **Integration Tests**: Using Testcontainers to spin up a real PostgreSQL 15 container, ensuring the repository layer and native SQL queries behave exactly as they would in production. 
- **API Tests**: Using **REST Assured** to validate HTTP status codes, JSON paths, and business constraints. 
- **Coverage**: Instrumented with **JaCoCo** to ensure high code 
  reliability (DTOs and Configs excluded for meaningful metrics).

## Getting Started
### Prerequisites
- JDK 21
- Docker
- Docker Compose

### Installation
1. Clone the repository
~~~bash
git clone https://github.com/massemiso/supermarket-api.git
~~~
2. Navigate to the folder and install dependencies with the maven wrapper
   included.
~~~bash
cd supermarket-api
./mvnw clean package
~~~
3. Build and run the docker compose
~~~bash
docker compose up --build
~~~
_This starts the Spring Boot app and a dedicated PostgreSQL 15 container._

## API Documentation
### Branches
| Method | Endpoint | Description | Return |
| :--- | :--- | :--- | :--- | 
| GET | `/api/branches` | Fetches all active branches (supports pagination) | OK 200 |
| GET | `/api/branches/{id}` | Gets details of a specific active branch | OK 200 |
| POST | `/api/branches` | Register a new branch with **name**, **address** and **phoneNumber** | CREATED 201 |
| PUT | `/api/branches/{id}` | Updates a specific active branch with a **name**, **address** and **phoneNumber** | OK 200 |
| DELETE | `/api/branches/{id}` | **Soft deletes** a specific active branch | NO_CONTENT 204 |

#### Example JSON Requests/Responses
- POST Body Request:
~~~json
{
  "name": "Walmart",
  "address": "Washington 3330",
  "phoneNumber": "123456789"
}
~~~
- POST Success Body Response:
~~~json
{
  "content": {
    "id": 1,
    "name": "Walmart",
    "address": "Washington 3330",
    "phoneNumber": "123456789"
  },
  "timestamp": "2026-04-22T12:00:00.000+00:00",
  "message": "Branch created successfully",
  "status": 201
}
~~~

### Products
| Method | Endpoint | Description | Return |
| :--- | :--- | :---| :--- |
| GET | `/api/products` | Fetches all active products (supports pagination) | OK 200 |
| GET | `/api/products/{id}` | Gets details of a specific active product | OK 200 |
| POST | `/api/products` | Register a new product with **name**, **category** and an **actualPrice** | CREATED 201 |
| PUT | `/api/products/{id}` | Updates a specific active product with a **name**, **category** and an **actualPrice** | OK 200 |
| DELETE | `/api/products/{id}` | **Soft deletes** a specific active product | NO_CONTENT 204 |

#### Example JSON Requests/Responses
- POST Body Request:
~~~json
{
  "name": "Organic Milk",
  "category": "Dairy",
  "actualPrice": 4.50
}
~~~
- POST Success Body Response:
~~~json
{
  "content": {
    "id": 1,
    "name": "Organic Milk",
    "category": "Dairy",
    "actualPrice": 4.50
  },
  "timestamp": "2026-04-22T12:00:00.000+00:00",
  "message": "Product created successfully",
  "status": 201
}
~~~

### Sales
| Method | Endpoint | Description | Return |
| :--- | :--- | :---| :--- |
| GET | `/api/sales` | Fetches all active sales (supports pagination and search filtering by **branchId** and **date**) | OK 200 |
| GET | `/api/sales/{id}` | Gets details of a specific active sale | OK 200 |
| POST | `/api/sales` | Register a new sale with an active **branchId** and a **list of detail sales**. Each detail sale includes **quantity** and an active **productId** | CREATED 201 |
| DELETE | `/api/sales/{id}` | **Soft deletes** a specific active sale | NO_CONTENT 204 |

#### Example JSON Requests/Responses
- POST Body Request:
~~~json
{
  "branchId": 1,
  "detailSaleRequestDtoList":
  [
    {
      "quantity": 5,
      "productId": 1
    },
    {
      "quantity": 1,
      "productId": 1
    }
  ]
}
~~~
- POST Success Body Response:
~~~json
{
  "content": {
    "id": 1,
    "date": "2026-04-22",
    "branchId": 1,
    "detailSaleResponseDtoList": 
    [
      {
        "id": 1,
        "quantity": 5,
        "unitPrice": 4.50,
        "productId": 1
      },
      {
        "id": 2,
        "quantity": 1,
        "unitPrice": 4.50,
        "productId": 1
      }
    ],
    "saleStatus": "REGISTERED",
    "total": 27.0
  },
  "timestamp": "2026-04-22T12:00:00.000+00:00",
  "message": "Sale created successfully",
  "status": 201
}
~~~

### Stats
| Method | Endpoint | Description                  | Return |
| :--- | :--- |:-----------------------------| :--- |
| GET | `/api/stats/best-selling-product` | Fetches best-selling product | OK 200 |
- GET Success Body Response:
~~~json
{
  "content": {
    "first": {
      "id": 1,
      "name": "Organic Milk",
      "category": "Dairy",
      "actualPrice": 4.50
    },
    "second": 27.0
  },
  "timestamp": "2026-04-22T12:00:00.000+00:00",
  "message": "Get best selling product successfully",
  "status": 200
}
~~~

### SwaggerUI
Once the application is running, you can access the interactive Swagger UI at:
`http://localhost:8080/swagger-ui.html`