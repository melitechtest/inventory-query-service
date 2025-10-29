# inventory-query-service (Servicio de Consulta y Lectura) 🔍

Este microservicio es el componente de **lectura** de la arquitectura CQRS. Es responsable de consumir eventos, actualizar su modelo de lectura (Redis) y servir consultas de stock con latencia ultra baja.

## 💡 Rol del Servicio

* **Patrón CQRS:** Eje del lado de la **Consulta (Read)**.
* **Responsabilidad:** Servir datos de stock desde Redis mediante consultas rápidas (`GET /stock/{id}`).
* **Asincronía:** Escuchar el **Topic** de ActiveMQ Artemis para actualizar su caché de Redis de forma reactiva.
* **Modelo de Lectura:** Utiliza Redis como una capa de persistencia desnormalizada y de alto rendimiento.

## ⚙️ Stack Tecnológico

* **Lenguaje:** Java 17
* **Framework:** Spring Boot 3.x
* **Modelo de Lectura:** Spring Data Redis + Redis 7
* **Mensajería:** **ActiveMQ Artemis (JMS)** usando `@JmsListener`.

## 🛠️ Configuración Local y Endpoints

El servicio se despliega a través de **Docker Compose** y se accede internamente en el puerto **8081**, enrutado externamente por el Nginx API Gateway.

| Endpoint | Método | Descripción |
| :--- | :--- | :--- |
| `/api/queries/stock/{id}` | `GET` | Recupera el stock actual de Redis. |

## 🚀 Integración con GitHub Actions

Este repositorio utiliza GitHub Actions para automatizar el ciclo de vida de desarrollo continuo (CI/CD): **Build, Test, y Push**.

### Flujo de CI/CD:

1.  **Activación:** Se activa con cada *push* al *branch* principal (`main`).
2.  **Build & Test:** Compila el código y ejecuta las pruebas unitarias.
3.  **Docker Build:** Construye la imagen de Docker para el servicio (`melitechtest/inventory-query-service:latest`).
4.  **Push:** Autentica y sube la imagen al repositorio de Docker Hub.