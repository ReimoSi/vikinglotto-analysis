# Vikinglotto Analysis

A small Spring Boot + HSQLDB application that demonstrates a clean Controller → Service → Repository architecture and covers:

- Vikinglotto draw CRUD
- Analysis (main/bonus frequencies, χ² statistic)
- Ticket generator (uniform and “anti-popular” methods)
- CSV import & export (draws and generated tickets)
- Swagger/OpenAPI docs
- Basic Auth with USER/ADMIN roles
- Unit + integration tests, GitHub Actions CI, JaCoCo coverage
- Helper: Python script to generate `data.sql` from CSV

---

## Table of Contents

1. [Overview](#overview)
2. [Prerequisites](#prerequisites)
3. [Getting Started](#getting-started)
    - [Clone](#clone)
    - [Build & Run](#build--run)
    - [Swagger UI](#swagger-ui)
4. [Configuration](#configuration)
5. [Database Initialization](#database-initialization)
6. [Database Structure (ERD)](#database-structure-erd)
7. [Authentication & Roles](#authentication--roles)
8. [API Overview](#api-overview)
9. [CSV Formats](#csv-formats)
10. [Tests, CI, and Coverage](#tests-ci-and-coverage)
11. [Project Structure](#project-structure)
12. [Architecture & Technologies](#architecture--technologies)
13. [Limitations & Next Steps](#limitations--next-steps)
14. [Handy Commands](#handy-commands)

---

## Overview

This app demonstrates a concise Controller → Service → Repository architecture on top of an in-memory HSQLDB. It exposes endpoints to manage draws, compute summary statistics, and generate deterministic or anti-popular tickets. CSV import/export round out the workflow.

---

## Prerequisites

- Java 21
- Gradle (or the included wrapper `./gradlew`)
- IntelliJ IDEA (recommended)
- (Optional) Python 3.11+ for the CSV → SQL helper script

---

## Getting Started

### Clone

```bash
git clone https://github.com/reimosi/vikinglotto-analysis.git
cd vikinglotto-analysis
```

### Build & Run

```bash
./gradlew bootRun
```

### Swagger UI

Open: `http://localhost:8080/swagger-ui/index.html`

---

## Configuration

Main settings live in `src/main/resources/application.properties`.

```properties
# HSQL (in-memory)
spring.datasource.url=jdbc:hsqldb:mem:mydb
spring.datasource.username=sa
spring.datasource.password=
spring.datasource.driver-class-name=org.hsqldb.jdbc.JDBCDriver

# Use schema.sql + data.sql
spring.jpa.hibernate.ddl-auto=none
spring.sql.init.mode=always

# Dev visibility
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

An embedded HSQL **TCP server** (for IDE DB tool) is started by `HsqlServerConfig` on port **9001**.  
Connect with: `jdbc:hsqldb:hsql://localhost:9001/mydb` (user: `sa`, empty password).

---

## Database Initialization

Spring Boot auto-runs these scripts on startup:

- **`schema.sql`** — creates tables and indexes
- **`data.sql`** — inserts initial sample rows

> **Note:** CSV imports do **not** modify `data.sql`; they populate the running in-memory database only.

---

## Database Structure (ERD)

**Table:** `draw`

| Column          | Type        | Notes                              |
|-----------------|-------------|------------------------------------|
| `id`            | BIGINT      | Identity, **PK**                   |
| `draw_id`       | VARCHAR(20) | **UNIQUE** business ID (date text) |
| `draw_date`     | DATE        |                                    |
| `main_numbers`  | VARCHAR(50) | e.g. `1 4 7 12 33 40`              |
| `bonus_numbers` | VARCHAR(10) | e.g. `2`                           |

ERD image (kept under source control):

![ERD](docs/lotto_erd.png)

---

## Authentication & Roles

Basic Auth (in-memory users):

- **USER** — `user / user123`
- **ADMIN** — `admin / admin123`

Access rules (see `SecurityConfig`):

- **Public:**  
  `/swagger-ui/**`, `/v3/api-docs/**`, `/actuator/health`, `/actuator/info`

- **USER or ADMIN:**  
  `GET /api/draws/**`, `GET /api/analysis/**`, `POST /api/generate/**`, `GET /api/generate/**`

- **ADMIN:**  
  `POST|PUT|DELETE /api/draws/**`, `POST /api/admin/**`

---

## API Overview

OpenAPI (Swagger UI): `http://localhost:8080/swagger-ui/index.html`

| Group         | Endpoint                            | Method | Role   | Description                              |
|---------------|-------------------------------------|--------|--------|------------------------------------------|
| **draws**     | `/api/draws`                        | GET    | USER   | List all draws                           |
|               | `/api/draws/{id}`                   | GET    | USER   | Get draw by ID                           |
|               | `/api/draws`                        | POST   | ADMIN  | Create a draw                            |
|               | `/api/draws/{id}`                   | PUT    | ADMIN  | Update a draw                            |
|               | `/api/draws/{id}`                   | DELETE | ADMIN  | Delete a draw                            |
|               | `/api/draws/export.csv`             | GET    | USER   | Export draws as CSV                      |
| **analysis**  | `/api/analysis/summary`             | GET    | USER   | Frequencies + χ² statistic               |
| **generator** | `/api/generate?method=&rows=&seed=` | POST   | USER   | Generate tickets                         |
|               | `/api/generate/export.csv?...`      | GET    | USER   | Export generated tickets as CSV          |
| **import**    | `/api/admin/import/csv`             | POST   | ADMIN  | Import draws from CSV (multipart upload) |
| **actuator**  | `/actuator/health`                  | GET    | PUBLIC | Liveness/Readiness health check          |

---

## CSV Formats

### Import (ADMIN)

`POST /api/admin/import/csv` — multipart/form-data, field **`file`**.  
Header must be exactly:

```
draw_id,draw_date,main_numbers,bonus_numbers
```

Rows example:

```
2024-01-03,2024-01-03,1 4 7 12 33 40,2
2024-01-10,2024-01-10,3 8 11 19 27 44,5
```

- `draw_id` — unique business ID (date string recommended)
- `draw_date` — **YYYY-MM-DD**
- `main_numbers` — 6 numbers (1..48), space-separated
- `bonus_numbers` — 1 number (1..5)

### Export — Draws

`GET /api/draws/export.csv`  
Header:

```
id,draw_id,draw_date,main_numbers,bonus_numbers
```

CSV characteristics:
- UTF-8 **BOM** (Excel-friendly)
- All values quoted; embedded quotes doubled
- `draw_id` and `draw_date` are Excel-safe: `="YYYY-MM-DD"`

### Export — Generated Tickets

`GET /api/generate/export.csv?method=uniform&rows=5&seed=42`  
Header:

```
main1,main2,main3,main4,main5,main6,bonus
```

Also UTF-8 BOM and quoted values.

---

## Tests, CI, and Coverage

### Run tests

```bash
./gradlew test
```

- **Unit:** `DrawServiceTest`
- **Integration:** `IntegrationTests` (random port) — verifies auth rules, imports, CSV exports, generator, analysis, actuator.
- **Quick local smoke (Windows):** `initial-tests.ps1`

### Coverage (JaCoCo)

```bash
./gradlew jacocoTestReport
# open build/reports/jacoco/test/html/index.html
```

### GitHub Actions CI

`.github/workflows/ci.yml`:

```yaml
name: CI
on: [push, pull_request]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '21'
          cache: gradle

      - run: chmod +x gradlew
      - run: ./gradlew clean test jacocoTestReport --no-daemon

      - name: Upload coverage report
        uses: actions/upload-artifact@v4
        with:
          name: jacoco-report
          path: build/reports/jacoco/test/html
```

---

## Project Structure

```text
vikinglotto-analysis/
├── README.md
├── build.gradle
├── gradlew / gradlew.bat
├── initial-tests.ps1                 # quick local smoke checks (PowerShell)
├── docs/
│   ├── erd.png                       # ERD screenshot
│   └── data/
│       ├── VIKINGLOTTO-full.csv      # example full dump (user-provided)
│       └── vikinglotto-sample-import.csv
├── tools/
│   └── csv_to_data_sql.py            # helper: CSV -> data.sql
├── src/
│   ├── main/
│   │   ├── java/ee/reimosi/lotto/
│   │   │   ├── LottoCrudApplication.java
│   │   │   ├── config/
│   │   │   │   ├── HsqlServerConfig.java
│   │   │   │   ├── OpenApiConfig.java
│   │   │   │   └── SecurityConfig.java
│   │   │   ├── common/RestExceptionHandler.java
│   │   │   ├── draw/ (entity + DTO + repo + mapper + service + controller)
│   │   │   ├── analysis/ (service + controller + dto)
│   │   │   └── generate/ (service + controller + dto)
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── schema.sql
│   │       └── data.sql
│   └── test/
│       └── java/ee/reimosi/lotto/
│           ├── IntegrationTests.java
│           └── draw/DrawServiceTest.java
└── .github/workflows/ci.yml
```

---

## Architecture & Technologies

- **Spring Boot, Java 21**
- **Spring Web, Spring Data JPA**
- **HSQLDB (in-memory)** + optional TCP server for IDE
- **MapStruct** for DTO mapping, **Lombok** for boilerplate
- **springdoc-openapi** (Swagger UI)
- **Spring Security** (Basic Auth)
- **JUnit 5, AssertJ, JaCoCo**
- **Gradle** (wrapper included)

**Design notes**
- `Draw` uses `@EqualsAndHashCode(of = "id")` — identity-based equality that plays well with JPA.
- `@Builder` is used where convenient (e.g., importer); elsewhere mapping is done with MapStruct.
- CSV exports include UTF-8 BOM and Excel-safe quoting to prevent Excel auto-formatting dates/IDs.

---

## Limitations & Next Steps

- In-memory DB resets on restart → consider PostgreSQL + Flyway.
- In-memory users → consider persistent users + JWT.
- Scraper is out of scope → add a separate CLI/service to fetch official results.
- Add pagination/sorting for large result sets.
- Strengthen validation (ranges, uniqueness of the 6 main numbers).
- Extend analysis (additional distributions, charts, trends).

---

## Handy Commands

**Health (public)**

```bash
curl -s http://localhost:8080/actuator/health
```

**Swagger UI**  
Open in browser: `http://localhost:8080/swagger-ui/index.html`

**List draws (USER)**

```bash
curl -u user:user123 http://localhost:8080/api/draws
```

**Create a draw (ADMIN)**

```bash
curl -u admin:admin123 -H "Content-Type: application/json"   -d '{"drawId":"2026-01-01","drawDate":"2026-01-01","mainNumbers":"2 6 11 23 45 48","bonusNumbers":"5"}'   http://localhost:8080/api/draws
```

**Generate tickets (deterministic)**

```bash
curl -u user:user123 -X POST   "http://localhost:8080/api/generate?method=uniform&rows=3&seed=42"
```

**Export generated tickets**

```bash
curl -u user:user123 -o generated.csv   "http://localhost:8080/api/generate/export.csv?method=uniform&rows=3&seed=42"
```

**Import CSV (ADMIN)**

```bash
curl -u admin:admin123 -F "file=@docs/data/vikinglotto-sample-import.csv;type=text/csv"   http://localhost:8080/api/admin/import/csv
```

**Export draws**

```bash
curl -u user:user123 -o draws.csv http://localhost:8080/api/draws/export.csv
```
