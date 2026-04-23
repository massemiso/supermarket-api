# Supermarket Management API
A REST API built with Spring Boot to manage supermarkets branches, products
and sales. This project was developed inspired by a technical test to
demonstrate skills in Java and Spring Boot.

## Features
- **CRUD** operations for Branches, Products and Sales.
- Statistics for best-selling products.
- **Soft deletion** of entities: to preserve data integrity and allow for data recovery.
- Pagination and search sorting for collections.
- Consistent API responses with **JSON**, including exception details.
- Dockerized application for easy deployment using **Docker**.
- Logging with **SLF4J** and **Logback**.

## Stack
- Java 21+
- Spring Boot 3.x
- Spring Boot JPA (Hibernate)
- PostgreSQL (production with docker) / H2 (local development)
- Lombok
- Maven

## Project Structure
~~~
src/main/java/com/massemiso/supermarket_api/
├── config/        # Web and Pagination configurations
├── controller/    # REST Endpoints
├── dto/           # Request/Response objects and Mappers
├── entity/        # Database Models (JPA Entities)
├── exception/     # Custom exceptions and Global Handler
├── repository/    # Data Access Layer (Native Queries & Projections)
├── service/       # Business Logic Layer
└── SupermarketApplication.java      # Main application class
~~~

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
./mvnw clean install
~~~
3. Build and run the docker compose
~~~bash
docker compose up --build
~~~

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