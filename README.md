# BFF - Backend For Frontend

Backend For Frontend para el proyecto **Sanos y Salvos**. Actua como capa de agregacion entre el frontend y los microservicios.

## Objetivo

El BFF centraliza las llamadas a multiples microservicios (Pet Service, Geo Service, Match Service) proporcionando una interfaz unificada para el frontend.

## Arquitectura

### Patron Aggregation
El BFF implementa el patron de diseno **Aggregation**, consolidando datos de multiples servicios en respuestas unificadas.

```
Frontend -> BFF -> Pet Service / Geo Service / Match Service
```

### Patrones de diseno implementados
- **Aggregation**: Consolida datos de Pet, Geo y Match en respuestas unificadas para el frontend.
- **Facade**: Proporciona una interfaz simplificada que oculta la complejidad de los microservicios subyacentes.
- **Proxy**: Actua como intermediario entre el frontend y los microservicios, desacoplando las comunicaciones.
- **DTO (Data Transfer Object)**: Utiliza objetos de transferencia para optimizar la serializacion y evitar exponer entidades JPA.

### Componentes

- [BffController](src/main/java/com/sanosysalvos/bff/controller/BffController.java): Endpoints REST principales
- [AggregationService](src/main/java/com/sanosysalvos/bff/service/AggregationService.java): Logica de agregacion
- [PetServiceClient](src/main/java/com/sanosysalvos/bff/client/PetServiceClient.java): Cliente HTTP para Pet Service
- [LocationServiceClient](src/main/java/com/sanosysalvos/bff/client/LocationServiceClient.java): Cliente HTTP para Geo Service
- [MatchServiceClient](src/main/java/com/sanosysalvos/bff/client/MatchServiceClient.java): Cliente HTTP para Match Service

## Endpoints

### Pets
| Metodo | Endpoint | Descripcion |
|--------|----------|-------------|
| GET | `/api/pets` | Listar todas las mascotas |
| GET | `/api/pets/{id}` | Obtener mascota por ID |
| POST | `/api/pets` | Crear nueva mascota |
| PUT | `/api/pets/{id}` | Actualizar mascota |
| DELETE | `/api/pets/{id}` | Eliminar mascota |
| GET | `/api/pets/search/status/{status}` | Filtrar mascotas por estado |
| GET | `/api/pets/search/race/{race}` | Filtrar mascotas por raza |
| GET | `/api/pets/search/color/{color}` | Filtrar mascotas por color |
| GET | `/api/pets/totals/status` | Obtener totales por estado |

### Locations
| Metodo | Endpoint | Descripcion |
|--------|----------|-------------|
| GET | `/api/locations` | Listar todas las ubicaciones |
| GET | `/api/locations/{id}` | Obtener ubicacion por ID |
| POST | `/api/locations` | Crear nueva ubicacion |
| PUT | `/api/locations/{id}` | Actualizar ubicacion |
| DELETE | `/api/locations/{id}` | Eliminar ubicacion |
| GET | `/api/locations/search/zone/{zone}` | Filtrar ubicaciones por zona |
| GET | `/api/locations/search/pet/{petId}` | Filtrar ubicaciones por mascota |
| GET | `/api/locations/totals/zone` | Obtener totales por zona |

### Matches
| Metodo | Endpoint | Descripcion |
|--------|----------|-------------|
| GET | `/api/matches` | Listar todas las coincidencias |
| GET | `/api/matches/{id}` | Obtener coincidencia por ID |
| POST | `/api/matches` | Crear nueva coincidencia |
| DELETE | `/api/matches/{id}` | Eliminar coincidencia |
| PUT | `/api/matches/{id}/confirm` | Confirmar coincidencia |
| PUT | `/api/matches/{id}/reject` | Rechazar coincidencia |
| GET | `/api/matching/search/status/{status}` | Filtrar coincidencias por estado |
| GET | `/api/matching/search/percentage/{percentage}` | Filtrar coincidencias por porcentaje |
| GET | `/api/matching/totals/status` | Obtener totales por estado |

### Dashboard
| Metodo | Endpoint | Descripcion |
|--------|----------|-------------|
| GET | `/api/dashboard` | Obtener datos del dashboard con metricas |
| GET | `/api/pets/{id}/with-location` | Obtener mascota con su ubicacion |
| POST | `/api/matching/run-automatic` | Ejecutar matching automatico |
| GET | `/api/health` | Health check del BFF |

## Tecnologias

- Java 17
- Spring Boot 3
- Spring Web (REST)
- RestTemplate (HTTP Client)
- Maven
- JaCoCo (cobertura de pruebas)

## Configuracion

```properties
# Puertos de servicios
pet.service.url=http://pet-service:3001
geo.service.url=http://geo-service:3002
match.service.url=http://match-service:3003

# Puerto del BFF
server.port=8081
```

## Instalacion

```bash
mvn clean install
mvn spring-boot:run
```

## Pruebas

```bash
# Ejecutar pruebas unitarias
mvn test

# Ejecutar pruebas con reporte de cobertura JaCoCo
mvn clean verify
# Reporte: target/site/jacoco/index.html
```

## Notas

- El BFF no implementa logica de negocio compleja, solo agregacion de datos.
- Implementa los patrones **Aggregation**, **Facade**, **Proxy** y **DTO**.
- Utiliza RestTemplate para comunicacion HTTP con los microservicios.
- Eureka Discovery: Configurado para registro automatico (pendiente activacion con servidor Eureka).

---

## Despliegue

Este servicio se despliega automaticamente como parte del repositorio **api-gateway** a la instancia **Edge (t3.small)**.

Ver [Setup Guide](../fullstack-ss-api-gateway/README.md#despliegue-en-aws-ec2) para detalles completos de la infraestructura.
