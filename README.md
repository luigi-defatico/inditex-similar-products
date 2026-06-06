# Similar Products API

REST API that exposes similar products for a given product ID.
Built as part of a backend technical assessment.

## Prerequisites

- Java 21+
- Maven (or use the included `./mvnw` wrapper)
- Docker

## Run the application

### Option A: locally

```bash
./mvnw spring-boot:run
```

### Option B: Docker Compose

First start the mock server from the `backendDevTest` repository:

```bash
docker-compose up -d simulado
```

Then build and start the application:

```bash
docker-compose up --build
```

The API will be available at `http://localhost:5000`.

## Run the tests

```bash
./mvnw test
```

## Self-evaluation with k6

Start the infrastructure from the `backendDevTest` repository:

```bash
docker-compose run --rm k6 run scripts/test.js
```

View results at `http://localhost:3000/d/Le2Ku9NMk/k6-performance-test`.

## Tech Stack

- Java 21 with virtual threads
- Spring Boot 4.0
- Spring WebFlux (WebClient for parallel HTTP calls)
- JUnit 5 + Mockito + WireMock

## Architecture

The project follows a DDD-inspired layered architecture:

- `domain` — core models and port interfaces
- `application` — use cases orchestrating domain logic
- `infrastructure` — HTTP adapters implementing domain ports
- `api` — REST controllers

## Endpoint

`GET /product/{productId}/similar` — returns a list of similar products with full detail.

Returns `404` if the product does not exist. Products whose detail cannot be fetched are silently excluded from the response.

## Design decisions

- Virtual threads (`spring.threads.virtual.enabled=true`) enable high concurrency without reactive complexity.
- Product details are fetched in parallel via `CompletableFuture` with a dedicated virtual thread executor.
- A configurable timeout (`product.api.timeout-ms`, default 2000ms) prevents slow upstream responses from blocking the thread pool.
- Unavailable products (404, 500, timeout) are excluded from the response rather than failing the entire request.