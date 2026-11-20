# GitHub Repository Searcher

Backend assignment: a Spring Boot application that searches GitHub repositories via the GitHub
REST API, persists the results into PostgreSQL, and exposes endpoints to retrieve the stored
results with optional filters. No UI — REST-compliant APIs, testable via Postman/Swagger UI.

## Package
`com.freightfox`

## Tech stack
- Java 21
- Spring Boot 3.3.5
- Spring Web, Spring Data JPA, Bean Validation
- PostgreSQL (JDBC)
- SpringDoc OpenAPI 3 / Swagger UI
- JUnit 5 + Mockito + AssertJ (unit tests)

## Prerequisites
- Java 21 (JDK)
- Apache Maven 3.x
- PostgreSQL server running locally on port 5432

## Database setup

Create the database referenced in `application.properties`:

```sql
CREATE DATABASE githubrepositorysearcher;
```

The datasource credentials (already configured in `src/main/resources/application.properties`):

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/githubrepositorysearcher
spring.datasource.username=postgres
spring.datasource.password=1234@
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=update
```

`hibernate.ddl-auto=update` will create the `repositories` table automatically on startup.
The table has a unique constraint on `github_id`, which enables **upsert** semantics: searching
for a repository already in the DB updates its details instead of duplicating it.

## Build & run

```bash
# from the project root (where pom.xml lives)
mvn clean install           # compiles, runs unit tests, packages
mvn spring-boot:run         # starts the application on http://localhost:8080
```

The application starts on port **8080**.

## API documentation (Swagger UI)

Once running, open:

```
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON:

```
http://localhost:8080/v3/api-docs
```

## API endpoints

### 1. Search GitHub repositories & persist results

```http
POST /api/github/search
Content-Type: application/json

{
  "query": "spring boot",
  "language": "Java",
  "sort": "stars"
}
```

- `query` (required): repository name / keywords — partial or full match
- `language` (optional): programming language filter, sent to GitHub as `language:X`
- `sort` (optional): `stars` | `forks` | `updated` (default `stars`)

Calls `https://api.github.com/search/repositories` and upserts results into PostgreSQL.

**Response (200):**
```json
{
  "message": "Repositories fetched and saved successfully",
  "repositories": [
    {
      "id": 1,
      "name": "spring-boot-example",
      "description": "An example repository for Spring Boot",
      "owner": "user123",
      "language": "Java",
      "stars": 450,
      "forks": 120,
      "lastUpdated": "2024-01-01T12:00:00Z"
    }
  ]
}
```

Edge cases handled:
- Invalid sort falls back to `stars`.
- Empty GitHub results return an empty list with an informative message.
- GitHub rate limits / API failures return a structured error (502).

---

### 2. Retrieve stored repositories

```http
GET /api/github/repositories?language=Java&minStars=100&sort=stars
```

- `language` (optional): filter by programming language (case-insensitive)
- `minStars` (optional): minimum stars count
- `sort` (optional): `stars` | `forks` | `updated` (default `stars`)

**Response (200):**
```json
{
  "repositories": [
    {
      "id": 1,
      "name": "spring-boot-example",
      "description": "...",
      "owner": "user123",
      "language": "Java",
      "stars": 450,
      "forks": 120,
      "lastUpdated": "2024-01-01T12:00:00Z"
    }
  ]
}
```

Filtering and sorting are applied at the database level (JPA Specifications + Sort).

## Error handling

All errors return a structured body:

```json
{
  "status": 400,
  "error": "Validation failed",
  "message": "Invalid request payload",
  "timestamp": "2024-01-01T12:00:00",
  "path": "/api/github/search",
  "details": ["query: query is required and must not be blank"]
}
```

## Running the tests

```bash
mvn test
```

Unit tests cover:
- `GithubClientTest` — successful search, empty results, rate-limit (HTTP 403), invalid JSON
- `GithubSearchServiceTest` — insert new repos, upsert (update-not-duplicate), empty results,
  invalid sort fallback
- `RepositoryServiceTest` — no-filter, language filter, minStars filter, sort by stars/forks/updated,
  invalid sort default, empty results, DTO mapping
- `GithubSearchControllerTest` / `RepositoryControllerTest` — happy path, validation (400),
  default sort
- `GlobalExceptionHandlerTest` — structured error responses for all exception types

## Postman

The endpoints are plain REST JSON endpoints and can be tested directly in Postman:

- `POST http://localhost:8080/api/github/search` with a JSON body
- `GET http://localhost:8080/api/github/repositories?language=Java&minStars=100&sort=stars`
