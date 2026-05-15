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

![Class Diagram](docs/class-diagram.png)

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
- Lombok & DTO Mapping using MapStruct

## Project Structure
~~~
src/main/java/com/massemiso/supermarket_api/
├── aspect/        # Aspect-Oriented Programming (Logging)
├── config/        # JPA Auditing, Web, Security & JWT filters
├── controller/    # REST Endpoints with @Valid and @PreAuthorize
├── dto/           # Records for immutable data transfer 
├── entity/        # JPA Models with @MappedSuperclass for Soft Delete
├── exception/     # Custom exceptions & @ControllerAdvice handler
├── mapper/        # Mapper Classes with MapStruct
├── repository/    # Data Access Layer (Native Queries & Projections)
├── service/       # Business Logic with @Transactional boundaries
├── util/          # Utilities used in other packages
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
- Docker & Docker Compose

### Installation & Deployment

1. **Clone the repository**
   ```bash
   git clone https://github.com/massemiso/supermarket-api.git
   cd supermarket-api
   ```

2. **Configure Environment Variables**
   The application requires several environment variables to run securely. Create a `.env` file in the root directory:
   ```bash
   cp .env.example .env
   ```
   Open `.env` and configure your database credentials and JWT secret. 

3. **Build the application**
   ```bash
   ./mvnw clean package -DskipTests
   ```

4. **Run with Docker Compose**
   ```bash
   docker compose up --build -d
   ```
   _This starts the Spring Boot app and a persistent PostgreSQL 15 container. The database data is stored in a Docker volume._

## Execution Modes

The application behavior changes significantly based on the `SPRING_PROFILES_ACTIVE` variable in your `.env`.

###  Development & Demo (Default)
**Best for:** Exploring features, running automated tests, or quick demos.
- **How-To:** Set `SPRING_PROFILES_ACTIVE=default` in `.env` or just don't create a `.env` file.
- **Database:** Uses H2 in-memory (or PostgreSQL if Docker is up).
- **Seeding:** Automatically populates the DB with mock branches, products, and sales.
- **Pre-seeded Users:**
    - `admin`, `manager`, `cashier`, `guest` (Passwords: `see below on API Documentation`).
- **Access:** Test all permission levels immediately via Swagger at `http://localhost:8080/swagger-ui/index.html`.

### Production (Secure)
**Best for:** Real-world deployment or a clean "production-like" test.
- **How-To:** Set `SPRING_PROFILES_ACTIVE=prod` in `.env`.
- **Database:** Connects to PostgreSQL.
- **Seeding:** No mock data is generated.
- **Security:** **Only one** admin user is created using the `PROD_ADMIN_USERNAME/PASSWORD` from your `.env`.
- **Note:** You must manually set up your branches and products after logging in as the Admin.

## Swagger UI
Once the application is running, you can access the interactive Swagger UI at:
`http://localhost:8080/swagger-ui/index.html`

![Swagger UI Example](docs/swaggerui.png)

## API Documentation

### Authentication & Security
The API is secured using **JWT (JSON Web Tokens)**. To access protected endpoints, you must include the token in the `Authorization` header as a `Bearer` token.

**Pre-seeded Users (Development Mode Only):**

| Username | Password | Role |
| :--- | :--- | :--- |
| `admin` | `admin123` | ADMIN |
| `manager` | `manager123` | MANAGER |
| `cashier` | `cashier123` | CASHIER |
| `guest` | `guest123` | GUEST |

*Note: In production mode, use the credentials defined in your `.env` file.*

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