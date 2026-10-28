# CSV Validator

A REST API that accepts CSV uploads, validates them against configurable rule sets, stores the results, and reports which rows failed and why.

## Features

- **Rule sets:** create, read, update and delete named rule sets. Each contains per-column rules: required, data type (integer, decimal, date, string), min/max, allowed values, regex pattern, and uniqueness within the file. 
- **Validation runs:** upload a CSV with a rule set ID; every row is validated and the run is stored. 
- **Reporting:** a run summary (total rows, passed, failed, counts per rule) and a paged list of row-level errors.
- **Input handling:** non-CSV files, empty files, oversized files and malformed CSVs are rejected with clear error respones.

## Tech stack

| Area | Choice |
|---|---|
| Language / build | Java 21, Maven (wrapper included) |
| Framework | Spring Boot 4.1.2 (Web, Data JPA, Validation) |
| Database | PostgreSQL, schema managed by Flyway |
| Testing | JUnit 5, Mockito, MockMvc, Testcontainers |
| Packaging | Docker, Docker Compose |
| CI | GitHub Actions |

## Data model

```
RuleSet 1 ──< Rule
RuleSet 1 ──< ValidationRun 1 ──< RowError
```

- RuleSet (id, name, description, created_at)
- Rule (id, rule_set_id, column_name, rule_type, parameters)
- ValidationRun (id, rule_set_id, filename, total_rows, failed_rows, status, created_at)
- RowError (id, run_id, row_number, column_name, rule_type, message)

## API
| Method | Path | Purpose |
|---|---|---|
| POST / GET | `/rule-sets` | Create / list rule sets |
| GET / PUT / DELETE | `/rule-sets/{id}` | Read / update / delete a rule set |
| POST | `/rule-sets/{id}/rules` | Add a rule to a rule set |
| DELETE | `/rules/{id}` | Delete a rule |
| POST | `/validation-runs` | Upload a CSV (`file`) with a `ruleSetId` |
| GET | `/validation-runs/{id}` | Run summary |
| GET | `/validation-runs/{id}/errors` | Paged row-level errors |

## Running locally
**Prerequisites** JDK 21, Docker (for the database and for tests)

1. Start a PostgreSQL container

docker run --name csvvalidator-db -e POSTGRES_DB=csvvalidator -e POSTGRES_USER=csvvalidator -e POSTGRES_PASSWORD=csvvalidator -p 5432:5432 -d postgres:16-alpine

2. Start the app: 

./mvnw spring-boot:run

Connection settings live in src/main/resources/application.yml can be overridden with the DB_URL, DB_USERNAME and DB_PASSWORD environment variables.

## Running tests
Docker must be running; integration tests start a throwaway PostgreSQL container via Testcontainers

./mvnw test