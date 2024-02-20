package it.sabato.pizzeria.config;

import it.sabato.pizzeria.config.OrderStatusConstants;

import java.util.UUID;

/**
 * The type Order status test constants.
 * @author Gianluca Sabato
 */
public class OrderStatusTestConstants extends OrderStatusConstants {
    /**
     * The constant RECEVIED_ID.
     */
    public static final UUID RECEVIED_ID = UUID.fromString("addf422c-4b37-4631-b0d0-3cfcbb68fe41");
    /**
     * The constant CANCELLED_ID.
     */
    public static final UUID CANCELLED_ID = UUID.fromString("df350171-e428-4d2c-a6c4-31123ef40ead");
    /**
     * The constant PROCESSING_ID.
     */
    public static final UUID PROCESSING_ID = UUID.fromString("6f0747ae-324e-4178-970d-9cda7cc03968");
    /**
     * The constant COMPLETED_ID.
     */
    public static final UUID COMPLETED_ID = UUID.fromString("cb90a068-10b8-4753-b55a-cdeadc2ef573");
    private OrderStatusTestConstants() {
        super();
    }
}
