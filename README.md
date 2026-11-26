# riu-backend-challenge

Backend con 2 servicios: uno para registrar una busqueda y otro para consultar una busqueda por id y contar cuantras otras busquedas hubieron.

## Stack

- Java 25
- Spring Boot 4.2 (Web MVC, Data JPA, Kafka, Validation)
- Oracle Database Free 23
- Apache Kafka 4.1
- Testcontainers, JUnit 5, Mockito, AssertJ, Awaitility, JaCoCo

## Cómo arrancar

### Todo con Docker

```bash
docker compose up --build
```

Levanta la app en `http://localhost:8080`, Kafka en `localhost:9092` y Oracle en `localhost:1521`.
Oracle tarda bastante en levantar. 

### App local con la infraestructura en Docker

```bash
docker compose up -d kafka oracle
```

```bash
./mvnw spring-boot:run
```

### Tests

Unitarios:

```bash
./mvnw test
```

Unitarios + integración (necesita Docker corriendo, usa Testcontainers):

```bash
./mvnw verify
```

Reporte de cobertura: `target/site/jacoco/index.html`.

## Mejoras pendientes

- Applicar patro outbox en la salida de mensaje a Kafka, garantiza o guarda la publicacion. (no tuve tiempo de agregarlo)
- Dead letter topic para mensajes que fallen al consumirse.
- Índice sobre las columnas que usa el conteo.
