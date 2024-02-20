package it.sabato.pizzeria.config;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * The type Pizza place docker test conf.
 *
 * @author Gianluca Sabato
 */
@Testcontainers
public abstract class PizzaPlaceDockerTestConf {
    /**
     * PostgreSQL TEst Container.
     */
    @Container
    @ServiceConnection
    public static final PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER = new PostgreSQLContainer<>(
            "postgres:16.2").withDatabaseName("integration-tests-db").withUsername("admin").withPassword("password")
            .withInitScript("init-test.sql");
}
