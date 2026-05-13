# Supermarket Management API
A REST API built with Spring Boot to manage supermarkets branches, products
and sales. This project was developed inspired by a technical test to
demonstrate skills in Java and Spring Boot.

## Features
- Role-based security using **Spring Security** and **JWT (JSON Web Tokens)**. Secures endpoints based on `ADMIN`, `MANAGER`, `CASHIER`, and `GUEST` roles.
- **Authentication System**: Login and Registration endpoints providing stateless JWT authentication.
- **CRUD** operations for Users, Branches, Products and Sales.
- **Business Statistics** for best-selling products via native SQL projections.
- **Soft deletion** of entities: to preserve data integrity and allow for data recovery.
- Pagination and search sorting for collections.
- Consistent API responses with **JSON**, including exception details.
- **Optimistic Locking** using JPA @Version to prevent data overwriting in high-load scenarios.
- Logging with **SLF4J** and **Logback**, including **Aspect-Oriented Logging (AOP)** for service monitoring.
- Automated auditing using **Spring Data JPA Auditing**.
- Dockerized application for easy deployment using **Docker**.

## Domain Model
Below is the class diagram representing the core entities and their relationships.

![Class Diagram](uml/class-diagram.png)

## Stack
- Java 21+
- Maven
- Spring Boot 3.x
- Spring Security & JWT
- Spring Boot JPA (Hibernate)
- PostgreSQL (production/docker) 
- H2 (local development/in-memory)
- Testcontainers
- REST Assured
- Docker & Docker Compose
- Lombok & Manual DTO Mapping

## Project Structure
~~~
src/main/java/com/massemiso/supermarket_api/
├── aspect/        # Aspect-Oriented Programming (Logging)
├── config/        # JPA Auditing, Web, Security & JWT filters
├── controller/    # REST Endpoints with @Valid and @PreAuthorize
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
## Configuration
The application can be configured using environment variables. This is especially important for security settings in production.

| Variable | Description                                      | Default (Local) |
| :--- |:-------------------------------------------------| :--- |
| `SPRING_DATASOURCE_URL` | JDBC URL for the db, useful in local development | `jdbc:h2:mem:supermarket-db` |
| `JWT_PRIVATE_KEY` | Secret key for signing JWT tokens                | `f043c350d733...` |
| `JWT_USER_GENERATOR` | Issuer name for the JWT token                    | `AUTH0-JWT-BACKEND` |
| `JWT_TOKEN_TIME` | Token expiration time in milliseconds            | `1800000` (30 min) |

## API Documentation

### Authentication & Security
The API is secured using **JWT (JSON Web Tokens)**. To access protected endpoints, you must first authenticate via the `/api/auth/login` endpoint to receive a token, which should then be included in the `Authorization` header as a `Bearer` token.

Unauthenticated requests will return a `401 Unauthorized`, and authenticated requests without the proper roles will return a standard `403 Forbidden` API response.

For testing purposes, the pre-seeded users are:

| Username | Password     | Role |
| :--- |:-------------| :--- |
| `admin` | `admin123`   | ADMIN |
| `manager` | `manager123` | MANAGER |
| `cashier` | `cashier123` | CASHIER |
| `guest` | `guest123` | GUEST |

### Auth
| Method | Endpoint | Description | Roles Required | Request Body | Return | Common Errors                                            |
| :--- | :--- |:--- |:---------------|:------------|:------------|:---------------------------------------------------------|
| POST | `/api/auth/login` | Authenticate and get a JWT token | `NONE` | `username`, `password` | OK 200 | 401 (Bad Credentials), 400 (Validation), 404 (Not Found) |
| POST | `/api/auth/register` | Register a new user as `GUEST` | `NONE` | `username`, `password`, `email` | CREATED 201 | 400 (Validation), 409 (User Exists)                      |

#### Example JSON Requests/Responses
- POST Login Request:
~~~json
{
  "username": "admin",
  "password": "admin123"
}
~~~
- POST Login Success Response:
~~~json
{
  "content": {
    "username": "admin",
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "status": true
  },
  "timestamp": "2026-05-12 22:30:00",
  "message": "Login successful",
  "status": 200
}
~~~
- POST Login Failure Response (Wrong Password):
~~~json
{
  "content": null,
  "timestamp": "2026-05-12 22:31:00",
  "message": "Bad credentials",
  "status": 401
}
~~~


### Users
| Method | Endpoint | Description | Roles Required | Request Body | Return | Common Errors |
| :--- | :--- |:--- |:---------------|:------------|:------------|:------------|
| GET | `/api/users` | Fetches all users (supports pagination) | `ADMIN`,`MANAGER` | `NONE` | OK 200 | `NONE` |
| GET | `/api/users/{id}` | Gets details of a specific user | `ADMIN`,`MANAGER` | `NONE` | OK 200 | 404 (Not Found) |
| POST | `/api/users` | Register a new user with roles | `ADMIN`,`MANAGER` | `username`, `password`, `email`, `roles` | CREATED 201 | 400 (Validation), 409 (Conflict) |
| PUT | `/api/users/{id}` | Updates a specific user | `ADMIN`,`MANAGER` | `username`, `password`, `email`, `roles` | OK 200 | 400 (Validation), 404 (Not Found) |
| DELETE | `/api/users/{id}` | **Soft deletes** a specific user | `ADMIN` | `NONE` | NO_CONTENT 204 | 404 (Not Found) |

### Branches
| Method | Endpoint | Description | Roles Required | Request Body | Return | Common Errors |
| :--- | :--- |:--- |:---------------|:------------|:------------|:------------|
| GET | `/api/branches` | Fetches all active branches (supports pagination) | `ANY`           | `NONE` | OK 200 | `NONE` |
| GET | `/api/branches/{id}` | Gets details of a specific active branch   | `ANY`           | `NONE` | OK 200 | 404 (Not Found) |
| POST | `/api/branches` | Register a new branch | `ADMIN`         | `name`, `address`, `phoneNumber` | CREATED 201 | 400 (Validation) |
| PUT | `/api/branches/{id}` | Updates a specific active branch |  `ADMIN`    | `name`, `address`, `phoneNumber` | OK 200 | 400 (Validation), 404 (Not Found) |
| DELETE | `/api/branches/{id}` | **Soft deletes** a specific active branch  |  `ADMIN`   | `NONE` | NO_CONTENT 204 | 404 (Not Found) |

### Products
| Method | Endpoint             | Description                                                                            | Roles Required    | Request Body | Return | Common Errors |
| :--- | :--- |:---------------------|:------------------|:------------|:------------|:------------|
| GET | `/api/products`      | Fetches all active products (supports pagination)                                      | `ANY`            | `NONE` | OK 200 | `NONE` |
| GET | `/api/products/{id}` | Gets details of a specific active product                                              | `ANY`            | `NONE` | OK 200 | 404 (Not Found) |
| POST | `/api/products`      | Register a new product | `ADMIN`,`MANAGER` | `name`, `category`, `actualPrice` | CREATED 201 | 400 (Validation) |
| PUT | `/api/products/{id}` | Updates a specific active product | `ADMIN`,`MANAGER` | `name`, `category`, `actualPrice` | OK 200 | 400 (Validation), 404 (Not Found) |
| DELETE | `/api/products/{id}` | **Soft deletes** a specific active product                                             | `ADMIN`,`MANAGER` | `NONE` | NO_CONTENT 204 | 404 (Not Found) |

### Sales
| Method | Endpoint          | Description                                                                                                                                        | Roles Required              | Request Body | Return | Common Errors |
| :--- | :--- |:------------------|:----------------------------|:------------|:------------|:------------|
| GET | `/api/sales`      | Fetches all active sales (supports pagination and search filtering) | `ADMIN`,`MANAGER`,`CASHIER` | `NONE` | OK 200 | `NONE` |
| GET | `/api/sales/{id}` | Gets details of a specific active sale                                                                                                             | `ADMIN`,`MANAGER`,`CASHIER` | `NONE` | OK 200 | 404 (Not Found) |
| POST | `/api/sales`      | Register a new sale | `ADMIN`,`MANAGER`,`CASHIER` | `branchId`, `detailSaleRequestDtoList` | CREATED 201 | 400 (Validation), 404 (Not Found) |
| DELETE | `/api/sales/{id}` | **Soft deletes** a specific active sale                                                                                                            | `ADMIN`,`MANAGER`          | `NONE` | NO_CONTENT 204 | 404 (Not Found) |
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
| Method | Endpoint | Description                   | Roles Required   | Return |
| :--- | :--- |:------------------------------|:-----------------| :--- |
| GET | `/api/stats/best-selling-product` | Fetches best-selling product | `ADMIN`,`MANAGER` | OK 200 |
- GET Success Body Response:
~~~json
{
  "content": {
    "product": {
      "id": 1,
      "name": "Organic Milk",
      "category": "Dairy",
      "actualPrice": 4.50
    },
    "totalRevenue": 27.0
  },
  "timestamp": "2026-04-22T12:00:00.000+00:00",
  "message": "Get best selling product successfully",
  "status": 200
}
~~~

### SwaggerUI
Once the application is running, you can access the interactive Swagger UI at:
`http://localhost:8080/swagger-ui.html`