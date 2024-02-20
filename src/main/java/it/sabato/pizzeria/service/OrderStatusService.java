package it.sabato.pizzeria.service;

import it.sabato.pizzeria.dto.OrderStatusDTO;
import it.sabato.pizzeria.factory.OrderStatusDTOFactory;
import it.sabato.pizzeria.model.Order;
import it.sabato.pizzeria.model.OrderStatus;
import it.sabato.pizzeria.repositories.OrderRepository;
import it.sabato.pizzeria.repositories.OrderStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * The type Order status service.
 * @author Gianluca Sabato
 */
@Service
@RequiredArgsConstructor
public class OrderStatusService {
    private final OrderStatusRepository orderStatusRepository;
    private final OrderRepository orderRepository;

    /**
     * Gets order statuses.
     *
     * @return the order statuses
     * @author Gianluca Sabato
     */
    public List<OrderStatusDTO> getOrderStatuses() {
        List<OrderStatus> orderStatuses = orderStatusRepository.findAll();

        return orderStatuses.stream().map(OrderStatusDTOFactory::getOrderStatusDTO).toList();
    }

    /**
     * Gets order status.
     *
     * @param id the order status id
     * @return the order status
     * @author Gianluca Sabato
     */
    public Optional<OrderStatusDTO> getOrderStatus(UUID id) {
        Optional<OrderStatusDTO> optionalOrderStatusDTO = Optional.empty();
        Optional<OrderStatus> orderStatusOptional = orderStatusRepository.findById(id);

        if (orderStatusOptional.isPresent()) {
            OrderStatus orderStatus = orderStatusOptional.get();
            OrderStatusDTO orderStatusDTO = OrderStatusDTOFactory.getOrderStatusDTO(orderStatus);

            optionalOrderStatusDTO = Optional.of(orderStatusDTO);
        }

        return optionalOrderStatusDTO;
    }

    /**
     * Gets order status for order id.
     *
     * @param orderId the order id
     * @return the order status for order id
     * @author Gianluca Sabato
     */
    public Optional<OrderStatusDTO> getOrderStatusForOrderId(UUID orderId) {
        Optional<OrderStatusDTO> optionalOrderStatusDTO = Optional.empty();
        Optional<Order> orderOptional = orderRepository.findById(orderId);

        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            OrderStatus orderStatus = order.getOrderStatus();

            if (orderStatus != null) {
                OrderStatusDTO orderStatusDTO = OrderStatusDTOFactory.getOrderStatusDTO(orderStatus);

                optionalOrderStatusDTO = Optional.of(orderStatusDTO);
            }
        }

        return optionalOrderStatusDTO;
    }
}
