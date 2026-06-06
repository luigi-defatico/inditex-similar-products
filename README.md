# Similar Products API

REST API that exposes similar products for a given product ID.
Built as part of a backend technical assessment.

## Prerequisites

- Java 21+
- Maven (or use the included `./mvnw` wrapper)
- Docker (for running the mock server and k6 tests)

## Run the application

```bash
./mvnw spring-boot:run
```

The API will be available at `http://localhost:5000`.

## Run the tests

```bash
./mvnw test
```

## Self-evaluation with k6

Start the mock server and infrastructure:

```bash
docker-compose up -d simulado influxdb grafana
```

Run the performance test:

```bash
docker-compose run --rm k6 run scripts/test.js
```

View results at `http://localhost:3000/d/Le2Ku9NMk/k6-performance-test`.

## Tech Stack

- Java 21 with virtual threads
- Spring Boot 3.4
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