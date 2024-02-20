package it.sabato.pizzeria.factory;

import it.sabato.pizzeria.dto.OrderStatusDTO;
import it.sabato.pizzeria.model.OrderStatus;

/**
 * The type Order status dto factory.
 * @author Gianluca Sabato
 */
public class OrderStatusDTOFactory {
    private OrderStatusDTOFactory() {
    }

    /**
     * Gets order status dto.
     *
     * @param orderStatus the order status
     * @return the order status dto
     * @author Gianluca Sabato
     */
    public static OrderStatusDTO getOrderStatusDTO(OrderStatus orderStatus) {
        return OrderStatusDTO.builder().orderStatusId(orderStatus.getOrderStatusId()).orderStatus(orderStatus.getStatus())
                .build();
    }
}
