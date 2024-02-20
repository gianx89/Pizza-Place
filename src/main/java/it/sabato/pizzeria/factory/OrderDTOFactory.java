package it.sabato.pizzeria.factory;

import it.sabato.pizzeria.dto.OrderDTO;
import it.sabato.pizzeria.model.Order;

/**
 * The type Order dto factory.
 * @author Gianluca Sabato
 */
public class OrderDTOFactory {
    private OrderDTOFactory() {
    }

    /**
     * Gets order dto.
     *
     * @param order the order
     * @return the order dto
     * @author Gianluca Sabato
     */
    public static OrderDTO getOrderDTO(Order order) {
        return OrderDTO.builder().orderId(order.getOrderId()).pizzas(order.getPizzas()).build();
    }
}
