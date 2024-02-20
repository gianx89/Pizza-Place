package it.sabato.pizzeria.config;


/**
 * OrderStatus values. The same values are loaded inside the database.
 * @author Gianluca Sabato
 */
public class OrderStatusConstants {
    /**
     * The constant RECEVIED.
     */
    public static final String RECEVIED = "RECEVIED";
    /**
     * The constant CANCELLED.
     */
    public static final String CANCELLED = "CANCELLED";
    /**
     * The constant PROCESSING.
     */
    public static final String PROCESSING = "PROCESSING";
    /**
     * The constant COMPLETED.
     */
    public static final String COMPLETED = "COMPLETED";

    /**
     * Instantiates a new Order status constants.
     */
    protected OrderStatusConstants() {
    }
}
