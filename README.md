
# Pizza Place

Spring Boot 3.2 Showcase Project for developing with:
- Maven
- Java 17
- Rest Services (spring-boot-starter-web)
- HATEOAS (spring-boot-starter-hateoas)
- Actuator (spring-boot-starter-actuator)
- JPA (spring-boot-starter-data-jpa)
- Docker Compose (spring-boot-docker-compose)
- Validation (spring-boot-starter-validation)
- Swagger/OpenAPI (springdoc-openapi-starter-webmvc-ui)
- PostgreSQL
- Lombok
- Javadoc (maven-javadoc-plugin)

Showcase Project for testing with:
- Junit Jupiter (spring-boot-starter-test)
- Test Containers (spring-boot-testcontainers, postgresql and junit-jupiter)

Maven Plug-ins included:
- spring-boot-maven-plugin
- jacoco-maven-plugin (Test coverage)
- gitflow-maven-plugin


## Run Locally


## Installation

1. Clone the project
2. Open it with your favourite IDE
3. Install Docker (Linux and macOS) or Docker Desktop/Rancher Desktop (Windows).
4. Start the Docker daemon.
5. Launch the program as a normal Java Application
6. Check the OpenAPI docs at http://localhost:8080/swagger-ui/index.html and test the API.


## Running Tests

Open a terminal, cd to the project directory and run the following command:

```bash
  mvn test
```

Or run the tests using your favorite IDE.

To check the code coverage of the project after running the tests, go to the *projectDir/target/site/jacoco* path and open the *index.html* file.

