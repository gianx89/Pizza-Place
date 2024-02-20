package it.sabato.pizzeria.service;

import it.sabato.pizzeria.config.OrderStatusConstants;
import it.sabato.pizzeria.dto.OrderDTO;
import it.sabato.pizzeria.dto.OrderStatusDTO;
import it.sabato.pizzeria.factory.OrderDTOFactory;
import it.sabato.pizzeria.model.Order;
import it.sabato.pizzeria.model.OrderStatus;
import it.sabato.pizzeria.repositories.OrderRepository;
import it.sabato.pizzeria.repositories.OrderStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.naming.ConfigurationException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * The type Order service.
 * @author Gianluca Sabato
 */
@Service
@RequiredArgsConstructor
public class OrderService {
    /**
     * The constant WRONG_CONFIGURATION_MESSAGE.
     */
    public final static String WRONG_CONFIGURATION_MESSAGE = "Missing or wrong configuration data";
    private final OrderRepository orderRepository;
    private final OrderStatusRepository orderStatusRepository;

    /**
     * Gets orders.
     *
     * @return the orders
     * @author Gianluca Sabato
     */
    public List<OrderDTO> getOrders() {
        List<Order> orders = orderRepository.findAll();

        return orders.stream().map(OrderDTOFactory::getOrderDTO).toList();
    }

    /**
     * Gets next order.
     *
     * @return the next order
     * @throws ConfigurationException the configuration exception (missing database configuration values)
     * @author Gianluca Sabato
     */
    public Optional<OrderDTO> getNextOrder() throws ConfigurationException {
        Optional<OrderDTO> nextOrderOptional = Optional.empty();
        List<OrderStatus> statuses = orderStatusRepository.findByStatus(OrderStatusConstants.RECEVIED);

        if (statuses != null && statuses.size() == 1) {
            OrderStatus received = statuses.get(0);
            List<Order> orders = orderRepository.findByOrderStatusOrderByCreatedDateAsc(received);

            if (orders != null && !orders.isEmpty()) {
                Order order = orders.get(0);
                OrderDTO nextOrder = OrderDTOFactory.getOrderDTO(order);

                nextOrderOptional = Optional.of(nextOrder);
            }
        } else {
            throw new ConfigurationException(WRONG_CONFIGURATION_MESSAGE);
        }

        return nextOrderOptional;
    }

    /**
     * Update the status of the current order (from PROCESSING to CLOSED) and then retrieve the next order to be
     * processed updating its status (from RECEIVED to PROCESSING).
     *
     * @return the optional
     * @throws ConfigurationException the configuration exception (missing database configuration values)
     * @author Gianluca Sabato
     */
    public Optional<OrderDTO> updateNextOrder() throws ConfigurationException {
        Optional<OrderDTO> nextOrderOptional = Optional.empty();
        List<OrderStatus> statuses = orderStatusRepository.findByStatus(OrderStatusConstants.RECEVIED);

        if (statuses != null && statuses.size() == 1) {
            OrderStatus received = statuses.get(0);
            statuses = orderStatusRepository.findByStatus(OrderStatusConstants.PROCESSING);

            if (statuses != null && statuses.size() == 1) {
                OrderStatus processing = statuses.get(0);

                statuses = orderStatusRepository.findByStatus(OrderStatusConstants.COMPLETED);

                if (statuses != null && statuses.size() == 1) {
                    OrderStatus completed = statuses.get(0);

                    List<Order> receivedOrders = orderRepository.findByOrderStatusOrderByCreatedDateAsc(received);
                    List<Order> processingOrders = orderRepository.findByOrderStatusOrderByCreatedDateAsc(processing);

                    if (receivedOrders != null && !receivedOrders.isEmpty()) {
                        Order receivedOrder = receivedOrders.get(0);

                        if (processingOrders != null && !processingOrders.isEmpty()) {
                            Order processingOrder = processingOrders.get(0);
                            processingOrder.setOrderStatus(completed);
                            orderRepository.save(processingOrder);
                        }

                        receivedOrder.setOrderStatus(processing);
                        receivedOrder = orderRepository.save(receivedOrder);

                        OrderDTO nextOrder = OrderDTOFactory.getOrderDTO(receivedOrder);
                        nextOrderOptional = Optional.of(nextOrder);
                    }
                } else {
                    throw new ConfigurationException(WRONG_CONFIGURATION_MESSAGE);
                }
            } else {
                throw new ConfigurationException(WRONG_CONFIGURATION_MESSAGE);
            }
        } else {
            throw new ConfigurationException(WRONG_CONFIGURATION_MESSAGE);
        }

        return nextOrderOptional;
    }

    /**
     * Gets order.
     *
     * @param id the order id
     * @return the order
     * @author Gianluca Sabato
     */
    public Optional<OrderDTO> getOrder(UUID id) {
        Optional<OrderDTO> optionalOrderDTO = Optional.empty();

        Optional<Order> orderOptional = orderRepository.findById(id);

        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            OrderDTO orderDTO = OrderDTOFactory.getOrderDTO(order);

            optionalOrderDTO = Optional.of(orderDTO);
        }

        return optionalOrderDTO;
    }

    /**
     * Create order.
     *
     * @param orderRequest the order request
     * @return the order dto
     * @throws ConfigurationException the configuration exception (missing database configuration values)
     * @author Gianluca Sabato
     */
    public OrderDTO createOrder(OrderDTO orderRequest) throws ConfigurationException {
        OrderDTO orderDTO;

        List<OrderStatus> orderStatuses = orderStatusRepository.findByStatus(OrderStatusConstants.RECEVIED);

        if (orderStatuses != null && orderStatuses.size() == 1) {
            OrderStatus received = orderStatuses.get(0);

            Order order = new Order();
            order.setPizzas(orderRequest.getPizzas());
            order.setOrderStatus(received);

            order = orderRepository.save(order);
            orderDTO = OrderDTOFactory.getOrderDTO(order);
        } else {
            throw new ConfigurationException(WRONG_CONFIGURATION_MESSAGE);
        }

        return orderDTO;
    }

    /**
     * Save order.
     *
     * @param orderRequest       the order request
     * @param orderStatusRequest the order status request
     * @throws ConfigurationException the configuration exception (missing database configuration values)
     * @author Gianluca Sabato
     */
    public void saveOrder(OrderDTO orderRequest, OrderStatusDTO orderStatusRequest) throws ConfigurationException {
        Optional<Order> orderOptional = orderRepository.findById(orderRequest.getOrderId());

        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            Optional<OrderStatus> orderStatusOptional = orderStatusRepository.findById(
                    orderStatusRequest.getOrderStatusId());

            if (orderStatusOptional.isPresent()) {
                OrderStatus orderStatus = orderStatusOptional.get();

                order.setOrderStatus(orderStatus);
                orderRepository.save(order);
            } else {
                throw new ConfigurationException(WRONG_CONFIGURATION_MESSAGE);
            }
        }

    }
}
