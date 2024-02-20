package it.sabato.pizzeria;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;

/**
 * The type Test pizza place application.
 * @author Gianluca Sabato
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestPizzaPlaceApplication {
    /**
     * The entry point of application.
     *
     * @param args the input arguments
     * @author Gianluca Sabato
     */
    public static void main(String[] args) {
        SpringApplication.from(PizzaPlaceApplication::main).with(TestPizzaPlaceApplication.class).run(args);
    }
}
