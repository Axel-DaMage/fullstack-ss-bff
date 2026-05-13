# BFF - Backend For Frontend

Backend For Frontend para el proyecto **Sanos y Salvos**. Actúa como聚合层 (capa de agregación) entre el frontend y los microservicios.

## Objetivo

El BFF centraliza las llamadas a múltiples microservicios (Pet Service, Geo Service, Match Service) proporcionando una interfaz unificada para el frontend.

## Arquitectura

### Patrón Aggregation
El BFF implementa el patrón de diseño **Aggregation**, consolidando datos de múltiples servicios en respuestas unificadas.

```
Frontend → BFF → Pet Service / Geo Service / Match Service
```

### Componentes

- [BffController](src/main/java/com/sanosysalvos/bff/controller/BffController.java): Endpoints REST principales
- [AggregationService](src/main/java/com/sanosysalvos/bff/service/AggregationService.java): Lógica de agregación
- [PetServiceClient](src/main/java/com/sanosysalvos/bff/client/PetServiceClient.java): Cliente HTTP para Pet Service
- [LocationServiceClient](src/main/java/com/sanosysalvos/bff/client/LocationServiceClient.java): Cliente HTTP para Geo Service
- [MatchServiceClient](src/main/java/com/sanosysalvos/bff/client/MatchServiceClient.java): Cliente HTTP para Match Service

## Endpoints

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/pets` | Listar todas las mascotas |
| GET | `/api/pets/{id}` | Obtener mascota por ID |
| POST | `/api/pets` | Crear nueva mascota |
| PUT | `/api/pets/{id}` | Actualizar mascota |
| DELETE | `/api/pets/{id}` | Eliminar mascota |
| GET | `/api/locations` | Listar ubicaciones |
| GET | `/api/locations/zone/{zone}` | Filtrar por zona |
| GET | `/api/matches` | Listar coincidencias |
| POST | `/api/matches` | Crear coincidencia |
| PUT | `/api/matches/{id}/confirm` | Confirmar coincidencia |
| PUT | `/api/matches/{id}/reject` | Rechazar coincidencia |
| GET | `/api/dashboard` | Obtener datos del dashboard |
| GET | `/api/pets/{id}/with-location` | Mascota con su ubicación |

## Tecnologías

- Java 17
- Spring Boot 3
- Spring Web (REST)
- RestTemplate (HTTP Client)
- Maven

## Configuración

```properties
# Puertos de servicios
pet.service.url=http://pet-service:8080
geo.service.url=http://geo-service:8082
match.service.url=http://match-service:3003

# Puerto del BFF
server.port=8081
```

## Instalación

```bash
mvn clean install
mvn spring-boot:run
```

## Pruebas

```bash
mvn test
```

## Notas

- El BFF no implementa lógica de negocio compleja, solo聚合 (agregación) de datos.
- Implementa el patrón de diseño **Facade** proporcionando una interfaz simplificada.
- Utiliza RestTemplate para comunicación HTTP con los microservicios.

---

## Despliegue

Este servicio se despliega automáticamente como parte del repositorio **api-gateway** a la instancia **Edge (t3.small)**.

Ver [Setup Guide](../fullstack-ss-api-gateway/README.md#despliegue-en-aws-ec2) para detalles completos de la infraestructura.