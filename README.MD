# Clean Architecture in Task Management Application

This project is a **Task Management Application** built with **Java Spring Boot**. It is designed to demonstrate the implementation of **Clean Architecture** to build maintainable, scalable, and testable applications. This project is an example of how to structure a Spring Boot application following Clean Architecture principles.

---

## What is Clean Architecture?

**Clean Architecture** is a software design philosophy introduced by **Robert C. Martin** (Uncle Bob). It emphasizes the separation of concerns and dependency inversion to ensure that the core logic of your application is isolated from external dependencies such as the database, frameworks, and user interfaces.

### Key Principles:
1. **Separation of Concerns**: Each layer or component has a distinct responsibility, making the code easier to understand and maintain.
2. **Dependency Rule**: The inner layers (business logic) should never depend on the outer layers (e.g., database, frameworks).
3. **Testability**: Each layer can be independently tested due to loose coupling between the layers.
4. **Independent of Frameworks**: The core logic does not rely on external frameworks or libraries, making it easier to swap or upgrade them.

---

## Why Use Clean Architecture?

- **Scalability**: Clean Architecture helps scale the application over time, as the separation of concerns makes adding new features easier without affecting other parts of the system.
- **Testability**: Because the business logic is decoupled from external concerns like frameworks or the database, it can be tested independently with unit tests.
- **Maintainability**: With clearly defined boundaries and well-organized code, it is easier to maintain and refactor the codebase.
- **Flexibility**: Allows flexibility in adapting new technologies without disturbing the core business logic of the application.

---

## Project Structure

The project is organized following **Clean Architecture** principles. Here's an overview of the folder structure:

*   `task-management-app/`
   *   `.gitignore`        # Git ignore file
   *   `LICENSE`           # License file (e.g., MIT License)
   *   `README.md`         # Project description and instructions (README)
   *   `pom.xml`           # Maven build file (for dependencies)
   *   `src/`
   * 
      *   `main/`
      * 
         *   `java/`
         * 
            *   `com/`
            * 
               *   `ltphat/`
               * 
                  *   `task_management/`
                  * 
                     *   `application/`      # Application Layer (Use Cases / Business Logic)
                     * 
                        *   `services/`     # Business logic and service classes
                        *   `dtos/`         # Data Transfer Objects (DTOs)
                        *   `mappers/`      # Object mapping (MapStruct interfaces)
                     *   `domain/`           # Domain Layer (Entities / Aggregates / Business Logic)
                     * 
                        *   `model/`        # Entity Models (JPA Entities)
                        *   `repository/`   # Repository interfaces (Data access)
                     *   `interfaces/`        # Interface Layer (Controllers, APIs)
                     * 
                        *   `api/`          # Controllers (REST API Endpoints)
                     *   `exception/`        # Exception Handling Layer (Global exception handling)
         *   `resources/`
         * 
            *   `application.yml` # Application configuration (Database connection, etc.)
            *   `static/`         # Static files (e.g., images, HTML, JavaScript)
            *   `templates/`      # Template files (e.g., Thymeleaf templates)
      *   `test/`
      * 
         *   `java/`
         * 
            *   `com/`
            * 
               *   `ltphat/`
               * 
                  *   `task_management/`
                  * 
                     *   `application/`      # Test cases for application layer (services, use cases)
                     *   `domain/`           # Test cases for domain layer (entities, repositories)
                     *   `infrastructure/`   # Test cases for infrastructure layer (DB, integrations)
                     *   `interface/`        # Test cases for interface layer (controllers)
         *   `resources/`
         * 
            *   `application.yml` # Test-specific configuration (mock database, etc.)

### Breakdown of the Layers:

1. **Application Layer** (`application`):
    - Contains the business logic of the application, including services, DTOs, and mappers. This is where you define use cases and business rules.

2. **Domain Layer** (`domain`):
    - Contains the core entities (e.g., `Task`) and repository interfaces for accessing the database. The domain layer should not depend on external frameworks or libraries.

3. **Infrastructure Layer** (`infrastructure`):
    - Contains the implementation of repository classes, database configurations, and other external services. This layer implements the interfaces defined in the domain layer.

4. **Interface Layer** (`interface`):
    - Contains the controllers (REST API endpoints). It acts as an interface between the application and the external world, such as the user interface or external systems.

5. **Exception Layer** (`exception`):
    - Handles global exception management and error response handling. This ensures that errors are returned in a consistent format.

6. **Config Layer** (`config`):
    - Contains application-wide configurations like MapStruct configurations, database connections, and other external service integrations.

---

## Database Connection Configuration

In this project, we use **YAML** (`application.yml`) instead of properties files for configuring the database connection. Here's an example configuration for **MySQL**:

### `src/main/resources/application.yml`

```yaml
spring:
  datasource:
    url: jdbc:mysql://${DB_HOST:localhost}:3306/${DB_NAME:taskdb}
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:rootpassword}
    driver-class-name: com.mysql.cj.jdbc.Driver

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

logging:
  level:
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql: TRACE
```

- The database configuration is **externalized using environment variables** (`DB_HOST`, `DB_NAME`, `DB_USERNAME`, and `DB_PASSWORD`) to keep sensitive information secure.

- **YAML** provides a cleaner and more structured way to configure Spring Boot compared to `application.properties`.

## How the Project Works
- **Task Management**: The main functionality is to manage tasks, including creating, updating, retrieving, and deleting tasks.

- **Pagination & Search**: The `getAllTasks` endpoint supports pagination, sorting, and searching.

- **Clean Architecture**: Each layer has a specific responsibility, with clear separation between business logic, database access, and user interface handling.

- **DTOs**: Data Transfer Objects (DTOs) are used to decouple the internal data model (`Task`) from the external representation exposed through the REST API.

## Acknowledgements
- **Spring Boot**: A powerful, feature-rich framework for building Java-based applications.

- **Clean Architecture**: For helping structure the application in a scalable, maintainable way.

- **MapStruct**: For simplifying object mapping between entities and DTOs.

- **Lombok**: For reducing boilerplate code in Java entities and DTOs.

Feel free to modify the text as needed to suit your project. Let me know if you need further adjustments!