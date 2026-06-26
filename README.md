# BFF

[![CI](https://github.com/Axel-DaMage/fullstack-ss-bff/actions/workflows/ci.yml/badge.svg)](https://github.com/Axel-DaMage/fullstack-ss-bff/actions/workflows/ci.yml)
[![Docker](https://github.com/Axel-DaMage/fullstack-ss-bff/actions/workflows/docker.yml/badge.svg)](https://github.com/Axel-DaMage/fullstack-ss-bff/actions/workflows/docker.yml)
![Java](https://img.shields.io/badge/java-17-orange)
![Spring Boot](https://img.shields.io/badge/spring%20boot-3.1.2-brightgreen)

Backend For Frontend. Aggregation layer between the frontend and microservices.

## Stack

- Java 17, Spring Boot 3.1.2
- RestTemplate, Spring Web
- Eureka Discovery Client
- Maven, JaCoCo

## Quick start

```bash
mvn clean install
mvn spring-boot:run
```

## Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/pets` | List pets |
| GET | `/api/pets/{id}` | Get pet |
| POST | `/api/pets` | Create pet |
| PUT | `/api/pets/{id}` | Update pet |
| DELETE | `/api/pets/{id}` | Delete pet |
| GET | `/api/locations` | List locations |
| GET | `/api/locations/zone/{zone}` | Filter by zone |
| GET | `/api/matches` | List matches |
| POST | `/api/matches` | Create match |
| PUT | `/api/matches/{id}/confirm` | Confirm match |
| PUT | `/api/matches/{id}/reject` | Reject match |
| GET | `/api/dashboard` | Dashboard data |
| GET | `/api/pets/{id}/with-location` | Pet with location |

## Tests

```bash
mvn test
mvn clean verify  # with JaCoCo report
```

## Architecture

Aggregation pattern: consolidates data from Pet, Geo, and Match services into unified responses for the frontend.
