package it.sabato.pizzeria;

import it.sabato.pizzeria.config.OrderStatusTestConstants;
import it.sabato.pizzeria.dto.ErrorDTO;
import it.sabato.pizzeria.dto.OrderDTO;
import it.sabato.pizzeria.dto.OrderStatusDTO;
import it.sabato.pizzeria.exception.RestResponseEntityExceptionHandler;
import it.sabato.pizzeria.factory.OrderDTOFactory;
import it.sabato.pizzeria.model.Order;
import it.sabato.pizzeria.model.OrderStatus;
import it.sabato.pizzeria.repositories.OrderRepository;
import it.sabato.pizzeria.repositories.OrderStatusRepository;
import it.sabato.pizzeria.service.OrderService;
import it.sabato.pizzeria.service.OrderStatusService;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import javax.naming.ConfigurationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static it.sabato.pizzeria.service.OrderService.WRONG_CONFIGURATION_MESSAGE;
import static org.mockito.Mockito.when;

/**
 * The type Pizza place unit tests.
 * @author Gianluca Sabato
 */
@ExtendWith(MockitoExtension.class)
class PizzaPlaceUnitTests {
    private final RestResponseEntityExceptionHandler handler = new RestResponseEntityExceptionHandler();
    private final MockHttpServletRequest servletRequest = new MockHttpServletRequest();
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderStatusRepository orderStatusRepository;
    @InjectMocks
    private OrderService orderService;
    @InjectMocks
    private OrderStatusService orderStatusService;

    // OrderService

    /**
     * Test get orders.
     * @author Gianluca Sabato
     */
    @Test
    public void testGetOrders() {
        List<String> pizzas = List.of("Margherita", "Diavola", "Tirolese");

        OrderStatus received = new OrderStatus();
        received.setStatus(OrderStatusTestConstants.RECEVIED);
        received.setOrderStatusId(OrderStatusTestConstants.RECEVIED_ID);

        Order order1 = new Order();
        order1.setOrderId(UUID.randomUUID());
        order1.setOrderStatus(received);
        order1.setPizzas(pizzas);

        Order order2 = new Order();
        order2.setOrderId(UUID.randomUUID());
        order2.setOrderStatus(received);
        order2.setPizzas(pizzas);

        List<Order> orders = List.of(order1, order2);

        when(orderRepository.findAll()).thenReturn(orders);

        List<OrderDTO> orderDTOS = orderService.getOrders();

        Assertions.assertNotNull(orderDTOS);
        Assertions.assertFalse(orderDTOS.isEmpty());

        for (int i = 0; i < orders.size(); i++) {
            OrderDTO orderDTO = orderDTOS.get(i);
            Assertions.assertNotNull(orderDTO.getOrderId());
            Assertions.assertNotNull(orderDTO.getPizzas());
            Assertions.assertFalse(orderDTO.getPizzas().isEmpty());
            Assertions.assertIterableEquals(orderDTOS.get(i).getPizzas(), orderDTO.getPizzas());
        }
    }

    /**
     * Test get next order.
     *
     * @throws ConfigurationException the configuration exception (missing database configuration values)
     * @author Gianluca Sabato
     */
    @Test
    public void testGetNextOrder() throws ConfigurationException {
        OrderStatus received = new OrderStatus();
        received.setStatus(OrderStatusTestConstants.RECEVIED);
        received.setOrderStatusId(OrderStatusTestConstants.RECEVIED_ID);
        List<OrderStatus> receivedList = List.of(received);

        when(orderStatusRepository.findByStatus(OrderStatusTestConstants.RECEVIED)).thenReturn(receivedList);

        List<String> pizzas = List.of("Margherita", "Diavola", "Tirolese");
        OrderDTO orderDTO = new OrderDTO(pizzas);

        Order order = new Order();
        order.setOrderId(UUID.randomUUID());
        order.setOrderStatus(received);
        order.setPizzas(pizzas);

        List<Order> orders = List.of(order);

        when(orderRepository.findByOrderStatusOrderByCreatedDateAsc(received)).thenReturn(orders);

        Optional<OrderDTO> nextOrderOptional = orderService.getNextOrder();

        Assertions.assertTrue(nextOrderOptional.isPresent());

        OrderDTO createdOrderDTO = nextOrderOptional.get();

        Assertions.assertNotNull(createdOrderDTO.getOrderId());
        Assertions.assertNotNull(createdOrderDTO.getPizzas());
        Assertions.assertFalse(createdOrderDTO.getPizzas().isEmpty());
        Assertions.assertIterableEquals(orderDTO.getPizzas(), createdOrderDTO.getPizzas());
    }

    /**
     * Test get next order with missing status configuration.
     * @author Gianluca Sabato
     */
    @Test
    public void testGetNextOrderMissingStatusConfiguration() {
        when(orderStatusRepository.findByStatus(OrderStatusTestConstants.RECEVIED)).thenReturn(new ArrayList<>());

        Exception exception = Assertions.assertThrows(ConfigurationException.class, () -> {
            orderService.getNextOrder();
        });

        String actualMessage = exception.getMessage();

        Assertions.assertTrue(actualMessage.contains(WRONG_CONFIGURATION_MESSAGE));
    }

    /**
     * Test get next order with null status configuration.
     * @author Gianluca Sabato
     */
    @Test
    public void testGetNextOrderNullStatusConfiguration() {
        when(orderStatusRepository.findByStatus(OrderStatusTestConstants.RECEVIED)).thenReturn(null);

        Exception exception = Assertions.assertThrows(ConfigurationException.class, () -> {
            orderService.getNextOrder();
        });

        String actualMessage = exception.getMessage();

        Assertions.assertTrue(actualMessage.contains(WRONG_CONFIGURATION_MESSAGE));
    }

    /**
     * Test get next order with wrong status configuration.
     * @author Gianluca Sabato
     */
    @Test
    public void testGetNextOrderWrongStatusConfiguration() {
        OrderStatus received = new OrderStatus();
        received.setStatus(OrderStatusTestConstants.RECEVIED);
        received.setOrderStatusId(OrderStatusTestConstants.RECEVIED_ID);

        OrderStatus duplicated = new OrderStatus();
        duplicated.setStatus(OrderStatusTestConstants.RECEVIED);
        duplicated.setOrderStatusId(UUID.randomUUID());

        List<OrderStatus> receivedList = List.of(received, duplicated);

        when(orderStatusRepository.findByStatus(OrderStatusTestConstants.RECEVIED)).thenReturn(receivedList);

        Exception exception = Assertions.assertThrows(ConfigurationException.class, () -> {
            orderService.getNextOrder();
        });

        String actualMessage = exception.getMessage();

        Assertions.assertTrue(actualMessage.contains(WRONG_CONFIGURATION_MESSAGE));
    }

    /**
     * Test get next order with empty orders list.
     *
     * @throws ConfigurationException the configuration exception (missing database configuration values)
     * @author Gianluca Sabato
     */
    @Test
    public void testGetNextOrderNotFound1() throws ConfigurationException {
        OrderStatus received = new OrderStatus();
        received.setStatus(OrderStatusTestConstants.RECEVIED);
        received.setOrderStatusId(OrderStatusTestConstants.RECEVIED_ID);
        List<OrderStatus> receivedList = List.of(received);

        when(orderStatusRepository.findByStatus(OrderStatusTestConstants.RECEVIED)).thenReturn(receivedList);

        when(orderRepository.findByOrderStatusOrderByCreatedDateAsc(received)).thenReturn(new ArrayList<>());

        Optional<OrderDTO> nextOrderOptional = orderService.getNextOrder();

        Assertions.assertTrue(nextOrderOptional.isEmpty());
    }

    /**
     * Test get next with null orders list.
     *
     * @throws ConfigurationException the configuration exception (missing database configuration values)
     * @author Gianluca Sabato
     */
    @Test
    public void testGetNextOrderNotFound2() throws ConfigurationException {
        OrderStatus received = new OrderStatus();
        received.setStatus(OrderStatusTestConstants.RECEVIED);
        received.setOrderStatusId(OrderStatusTestConstants.RECEVIED_ID);
        List<OrderStatus> receivedList = List.of(received);

        when(orderStatusRepository.findByStatus(OrderStatusTestConstants.RECEVIED)).thenReturn(receivedList);

        when(orderRepository.findByOrderStatusOrderByCreatedDateAsc(received)).thenReturn(null);

        Optional<OrderDTO> nextOrderOptional = orderService.getNextOrder();

        Assertions.assertTrue(nextOrderOptional.isEmpty());
    }

    /**
     * Test update next order.
     *
     * @throws ConfigurationException the configuration exception (missing database configuration values)
     * @author Gianluca Sabato
     */
    @Test
    public void testUpdateNextOrder() throws ConfigurationException {
        OrderStatus received = new OrderStatus();
        received.setStatus(OrderStatusTestConstants.RECEVIED);
        received.setOrderStatusId(OrderStatusTestConstants.RECEVIED_ID);
        List<OrderStatus> receivedList = List.of(received);

        when(orderStatusRepository.findByStatus(OrderStatusTestConstants.RECEVIED)).thenReturn(receivedList);

        OrderStatus processing = new OrderStatus();
        processing.setStatus(OrderStatusTestConstants.PROCESSING);
        processing.setOrderStatusId(OrderStatusTestConstants.PROCESSING_ID);
        List<OrderStatus> processingList = List.of(processing);

        when(orderStatusRepository.findByStatus(OrderStatusTestConstants.PROCESSING)).thenReturn(processingList);

        OrderStatus completed = new OrderStatus();
        completed.setStatus(OrderStatusTestConstants.COMPLETED);
        completed.setOrderStatusId(OrderStatusTestConstants.COMPLETED_ID);
        List<OrderStatus> completedList = List.of(completed);

        when(orderStatusRepository.findByStatus(OrderStatusTestConstants.COMPLETED)).thenReturn(completedList);

        Order receivedOrder = new Order();
        receivedOrder.setOrderId(UUID.randomUUID());
        receivedOrder.setPizzas(List.of("margherita"));
        receivedOrder.setOrderStatus(received);
        List<Order> receivedOrders = List.of(receivedOrder);

        when(orderRepository.findByOrderStatusOrderByCreatedDateAsc(received)).thenReturn(receivedOrders);

        Order processingOrder = new Order();
        processingOrder.setOrderId(UUID.randomUUID());
        processingOrder.setPizzas(List.of("diavola"));
        processingOrder.setOrderStatus(processing);
        List<Order> processingOrders = List.of(processingOrder);

        when(orderRepository.findByOrderStatusOrderByCreatedDateAsc(processing)).thenReturn(processingOrders);

        Order completedOrder = new Order();
        completedOrder.setOrderId(processingOrder.getOrderId());
        completedOrder.setPizzas(List.of("diavola"));
        completedOrder.setOrderStatus(completed);

        when(orderRepository.save(processingOrder)).thenReturn(completedOrder);

        Order newProcessingOrder = new Order();
        newProcessingOrder.setOrderId(receivedOrder.getOrderId());
        newProcessingOrder.setPizzas(List.of("margherita"));
        newProcessingOrder.setOrderStatus(processing);

        when(orderRepository.save(receivedOrder)).thenReturn(newProcessingOrder);

        Optional<OrderDTO> nextOrderOptional = orderService.updateNextOrder();

        Assertions.assertTrue(nextOrderOptional.isPresent());

        OrderDTO nextOrderDTO = nextOrderOptional.get();

        Assertions.assertNotNull(nextOrderDTO.getOrderId());
        Assertions.assertEquals(nextOrderDTO.getOrderId(), receivedOrder.getOrderId());
        Assertions.assertNotNull(nextOrderDTO.getPizzas());
        Assertions.assertFalse(nextOrderDTO.getPizzas().isEmpty());
        Assertions.assertIterableEquals(nextOrderDTO.getPizzas(), receivedOrder.getPizzas());
    }

    /**
     * Test update next order when the processing orders list is null.
     *
     * @throws ConfigurationException the configuration exception (missing database configuration values)
     * @author Gianluca Sabato
     */
    @Test
    public void testUpdateNextOrderNoProcessingOrdersIsNull() throws ConfigurationException {
        OrderStatus received = new OrderStatus();
        received.setStatus(OrderStatusTestConstants.RECEVIED);
        received.setOrderStatusId(OrderStatusTestConstants.RECEVIED_ID);
        List<OrderStatus> receivedList = List.of(received);

        when(orderStatusRepository.findByStatus(OrderStatusTestConstants.RECEVIED)).thenReturn(receivedList);

        OrderStatus processing = new OrderStatus();
        processing.setStatus(OrderStatusTestConstants.PROCESSING);
        processing.setOrderStatusId(OrderStatusTestConstants.PROCESSING_ID);
        List<OrderStatus> processingList = List.of(processing);

        when(orderStatusRepository.findByStatus(OrderStatusTestConstants.PROCESSING)).thenReturn(processingList);

        OrderStatus completed = new OrderStatus();
        completed.setStatus(OrderStatusTestConstants.COMPLETED);
        completed.setOrderStatusId(OrderStatusTestConstants.COMPLETED_ID);
        List<OrderStatus> completedList = List.of(completed);

        when(orderStatusRepository.findByStatus(OrderStatusTestConstants.COMPLETED)).thenReturn(completedList);

        Order receivedOrder = new Order();
        receivedOrder.setOrderId(UUID.randomUUID());
        receivedOrder.setPizzas(List.of("margherita"));
        receivedOrder.setOrderStatus(received);
        List<Order> receivedOrders = List.of(receivedOrder);

        when(orderRepository.findByOrderStatusOrderByCreatedDateAsc(received)).thenReturn(receivedOrders);

        when(orderRepository.findByOrderStatusOrderByCreatedDateAsc(processing)).thenReturn(null);

        Order newProcessingOrder = new Order();
        newProcessingOrder.setOrderId(receivedOrder.getOrderId());
        newProcessingOrder.setPizzas(List.of("margherita"));
        newProcessingOrder.setOrderStatus(processing);

        when(orderRepository.save(receivedOrder)).thenReturn(newProcessingOrder);

        Optional<OrderDTO> nextOrderOptional = orderService.updateNextOrder();

        Assertions.assertTrue(nextOrderOptional.isPresent());

        OrderDTO nextOrderDTO = nextOrderOptional.get();

        Assertions.assertNotNull(nextOrderDTO.getOrderId());
        Assertions.assertEquals(nextOrderDTO.getOrderId(), receivedOrder.getOrderId());
        Assertions.assertNotNull(nextOrderDTO.getPizzas());
        Assertions.assertFalse(nextOrderDTO.getPizzas().isEmpty());
        Assertions.assertIterableEquals(nextOrderDTO.getPizzas(), receivedOrder.getPizzas());
    }

    /**
     * Test update next order when the processing orders list is empty.
     *
     * @throws ConfigurationException the configuration exception (missing database configuration values)
     * @author Gianluca Sabato
     */
    @Test
    public void testUpdateNextOrderNoProcessingOrders() throws ConfigurationException {
        OrderStatus received = new OrderStatus();
        received.setStatus(OrderStatusTestConstants.RECEVIED);
        received.setOrderStatusId(OrderStatusTestConstants.RECEVIED_ID);
        List<OrderStatus> receivedList = List.of(received);

        when(orderStatusRepository.findByStatus(OrderStatusTestConstants.RECEVIED)).thenReturn(receivedList);

        OrderStatus processing = new OrderStatus();
        processing.setStatus(OrderStatusTestConstants.PROCESSING);
        processing.setOrderStatusId(OrderStatusTestConstants.PROCESSING_ID);
        List<OrderStatus> processingList = List.of(processing);

        when(orderStatusRepository.findByStatus(OrderStatusTestConstants.PROCESSING)).thenReturn(processingList);

        OrderStatus completed = new OrderStatus();
        completed.setStatus(OrderStatusTestConstants.COMPLETED);
        completed.setOrderStatusId(OrderStatusTestConstants.COMPLETED_ID);
        List<OrderStatus> completedList = List.of(completed);

        when(orderStatusRepository.findByStatus(OrderStatusTestConstants.COMPLETED)).thenReturn(completedList);

        Order receivedOrder = new Order();
        receivedOrder.setOrderId(UUID.randomUUID());
        receivedOrder.setPizzas(List.of("margherita"));
        receivedOrder.setOrderStatus(received);
        List<Order> receivedOrders = List.of(receivedOrder);

        when(orderRepository.findByOrderStatusOrderByCreatedDateAsc(received)).thenReturn(receivedOrders);

        when(orderRepository.findByOrderStatusOrderByCreatedDateAsc(processing)).thenReturn(new ArrayList<>());

        Order newProcessingOrder = new Order();
        newProcessingOrder.setOrderId(receivedOrder.getOrderId());
        newProcessingOrder.setPizzas(List.of("margherita"));
        newProcessingOrder.setOrderStatus(processing);

        when(orderRepository.save(receivedOrder)).thenReturn(newProcessingOrder);

        Optional<OrderDTO> nextOrderOptional = orderService.updateNextOrder();

        Assertions.assertTrue(nextOrderOptional.isPresent());

        OrderDTO nextOrderDTO = nextOrderOptional.get();

        Assertions.assertNotNull(nextOrderDTO.getOrderId());
        Assertions.assertEquals(nextOrderDTO.getOrderId(), receivedOrder.getOrderId());
        Assertions.assertNotNull(nextOrderDTO.getPizzas());
        Assertions.assertFalse(nextOrderDTO.getPizzas().isEmpty());
        Assertions.assertIterableEquals(nextOrderDTO.getPizzas(), receivedOrder.getPizzas());
    }

    /**
     * Test update next order with empty order statuses list (RECEIVED).
     * @author Gianluca Sabato
     */
    @Test
    public void testUpdateNextOrderMissingStatusConfigurationReceived() {
        when(orderStatusRepository.findByStatus(OrderStatusTestConstants.RECEVIED)).thenReturn(new ArrayList<>());

        Exception exception = Assertions.assertThrows(ConfigurationException.class, () -> {
            orderService.updateNextOrder();
        });

        String actualMessage = exception.getMessage();

        Assertions.assertTrue(actualMessage.contains(WRONG_CONFIGURATION_MESSAGE));
    }

    /**
     * Test update next order with empty order statuses list (PROCESSING).
     * @author Gianluca Sabato
     */
    @Test
    public void testUpdateNextOrderMissingStatusConfigurationProcessing() {
        OrderStatus received = new OrderStatus();
        received.setStatus(OrderStatusTestConstants.RECEVIED);
        received.setOrderStatusId(OrderStatusTestConstants.RECEVIED_ID);
        List<OrderStatus> receivedList = List.of(received);

        when(orderStatusRepository.findByStatus(OrderStatusTestConstants.RECEVIED)).thenReturn(receivedList);

        when(orderStatusRepository.findByStatus(OrderStatusTestConstants.PROCESSING)).thenReturn(new ArrayList<>());

        Exception exception = Assertions.assertThrows(ConfigurationException.class, () -> {
            orderService.updateNextOrder();
        });

        String actualMessage = exception.getMessage();

        Assertions.assertTrue(actualMessage.contains(WRONG_CONFIGURATION_MESSAGE));
    }

    /**
     * Test update next order with empty order statuses list (COMPLETED).
     * @author Gianluca Sabato
     */
    @Test
    public void testUpdateNextOrderMissingStatusConfigurationCompleted() {
        OrderStatus received = new OrderStatus();
        received.setStatus(OrderStatusTestConstants.RECEVIED);
        received.setOrderStatusId(OrderStatusTestConstants.RECEVIED_ID);
        List<OrderStatus> receivedList = List.of(received);

        when(orderStatusRepository.findByStatus(OrderStatusTestConstants.RECEVIED)).thenReturn(receivedList);
        OrderStatus processing = new OrderStatus();
        processing.setStatus(OrderStatusTestConstants.PROCESSING);
        processing.setOrderStatusId(OrderStatusTestConstants.PROCESSING_ID);

        List<OrderStatus> processingList = List.of(processing);

        when(orderStatusRepository.findByStatus(OrderStatusTestConstants.PROCESSING)).thenReturn(processingList);

        when(orderStatusRepository.findByStatus(OrderStatusTestConstants.COMPLETED)).thenReturn(new ArrayList<>());

        Exception exception = Assertions.assertThrows(ConfigurationException.class, () -> {
            orderService.updateNextOrder();
        });

        String actualMessage = exception.getMessage();

        Assertions.assertTrue(actualMessage.contains(WRONG_CONFIGURATION_MESSAGE));
    }

    /**
     * Test update next order with null order statuses list (RECEIVED).
     * @author Gianluca Sabato
     */
    @Test
    public void testUpdateNextOrderNullStatusConfigurationReceived() {
        when(orderStatusRepository.findByStatus(OrderStatusTestConstants.RECEVIED)).thenReturn(null);

        Exception exception = Assertions.assertThrows(ConfigurationException.class, () -> {
            orderService.updateNextOrder();
        });

        String actualMessage = exception.getMessage();

        Assertions.assertTrue(actualMessage.contains(WRONG_CONFIGURATION_MESSAGE));
    }

    /**
     * Test update next order with null order statuses list (PROCESSING).
     * @author Gianluca Sabato
     */
    @Test
    public void testUpdateNextOrderNullStatusConfigurationProcessing() {
        OrderStatus received = new OrderStatus();
        received.setStatus(OrderStatusTestConstants.RECEVIED);
        received.setOrderStatusId(OrderStatusTestConstants.RECEVIED_ID);
        List<OrderStatus> receivedList = List.of(received);

        when(orderStatusRepository.findByStatus(OrderStatusTestConstants.RECEVIED)).thenReturn(receivedList);

        when(orderStatusRepository.findByStatus(OrderStatusTestConstants.PROCESSING)).thenReturn(null);

        Exception exception = Assertions.assertThrows(ConfigurationException.class, () -> {
            orderService.updateNextOrder();
        });

        String actualMessage = exception.getMessage();

        Assertions.assertTrue(actualMessage.contains(WRONG_CONFIGURATION_MESSAGE));
    }

    /**
     * Test update next order with null order statuses list (COMPLETED).
     * @author Gianluca Sabato
     */
    @Test
    public void testUpdateNextOrderNullStatusConfigurationCompleted() {
        OrderStatus received = new OrderStatus();
        received.setStatus(OrderStatusTestConstants.RECEVIED);
        received.setOrderStatusId(OrderStatusTestConstants.RECEVIED_ID);
        List<OrderStatus> receivedList = List.of(received);

        when(orderStatusRepository.findByStatus(OrderStatusTestConstants.RECEVIED)).thenReturn(receivedList);

        OrderStatus processing = new OrderStatus();
        processing.setStatus(OrderStatusTestConstants.PROCESSING);
        processing.setOrderStatusId(OrderStatusTestConstants.PROCESSING_ID);

        List<OrderStatus> processingList = List.of(processing);

        when(orderStatusRepository.findByStatus(OrderStatusTestConstants.PROCESSING)).thenReturn(processingList);

        when(orderStatusRepository.findByStatus(OrderStatusTestConstants.COMPLETED)).thenReturn(null);

        Exception exception = Assertions.assertThrows(ConfigurationException.class, () -> {
            orderService.updateNextOrder();
        });

        String actualMessage = exception.getMessage();

        Assertions.assertTrue(actualMessage.contains(WRONG_CONFIGURATION_MESSAGE));
    }

    /**
     * Test update next order with wrong status configuration (RECEIVED).
     * @author Gianluca Sabato
     */
    @Test
    public void testUpdateNextOrderWrongStatusConfigurationReceived() {
        OrderStatus received = new OrderStatus();
        received.setStatus(OrderStatusTestConstants.RECEVIED);
        received.setOrderStatusId(OrderStatusTestConstants.RECEVIED_ID);

        OrderStatus duplicated = new OrderStatus();
        duplicated.setStatus(OrderStatusTestConstants.RECEVIED);
        duplicated.setOrderStatusId(UUID.randomUUID());

        List<OrderStatus> receivedList = List.of(received, duplicated);

        when(orderStatusRepository.findByStatus(OrderStatusTestConstants.RECEVIED)).thenReturn(receivedList);

        Exception exception = Assertions.assertThrows(ConfigurationException.class, () -> {
            orderService.updateNextOrder();
        });

        String actualMessage = exception.getMessage();

        Assertions.assertTrue(actualMessage.contains(WRONG_CONFIGURATION_MESSAGE));
    }

    /**
     * Test update next order with wrong status configuration (PROCESSING).
     * @author Gianluca Sabato
     */
    @Test
    public void testUpdateNextOrderWrongStatusConfigurationProcessing() {
        OrderStatus received = new OrderStatus();
        received.setStatus(OrderStatusTestConstants.RECEVIED);
        received.setOrderStatusId(OrderStatusTestConstants.RECEVIED_ID);

        List<OrderStatus> receivedList = List.of(received);

        when(orderStatusRepository.findByStatus(OrderStatusTestConstants.RECEVIED)).thenReturn(receivedList);

        OrderStatus processing = new OrderStatus();
        processing.setStatus(OrderStatusTestConstants.PROCESSING);
        processing.setOrderStatusId(OrderStatusTestConstants.PROCESSING_ID);

        OrderStatus duplicated = new OrderStatus();
        duplicated.setStatus(OrderStatusTestConstants.PROCESSING);
        duplicated.setOrderStatusId(UUID.randomUUID());

        List<OrderStatus> processingList = List.of(processing, duplicated);

        when(orderStatusRepository.findByStatus(OrderStatusTestConstants.PROCESSING)).thenReturn(processingList);

        Exception exception = Assertions.assertThrows(ConfigurationException.class, () -> {
            orderService.updateNextOrder();
        });

        String actualMessage = exception.getMessage();

        Assertions.assertTrue(actualMessage.contains(WRONG_CONFIGURATION_MESSAGE));
    }

    /**
     * Test update next order with wrong status configuration (COMPLETED).
     * @author Gianluca Sabato
     */
    @Test
    public void testUpdateNextOrderWrongStatusConfigurationCompleted() {
        OrderStatus received = new OrderStatus();
        received.setStatus(OrderStatusTestConstants.RECEVIED);
        received.setOrderStatusId(OrderStatusTestConstants.RECEVIED_ID);

        List<OrderStatus> receivedList = List.of(received);

        when(orderStatusRepository.findByStatus(OrderStatusTestConstants.RECEVIED)).thenReturn(receivedList);

        OrderStatus processing = new OrderStatus();
        processing.setStatus(OrderStatusTestConstants.PROCESSING);
        processing.setOrderStatusId(OrderStatusTestConstants.PROCESSING_ID);

        List<OrderStatus> processingList = List.of(processing);

        when(orderStatusRepository.findByStatus(OrderStatusTestConstants.PROCESSING)).thenReturn(processingList);

        OrderStatus completed = new OrderStatus();
        completed.setStatus(OrderStatusTestConstants.COMPLETED);
        completed.setOrderStatusId(OrderStatusTestConstants.COMPLETED_ID);

        OrderStatus duplicated = new OrderStatus();
        duplicated.setStatus(OrderStatusTestConstants.COMPLETED);
        duplicated.setOrderStatusId(UUID.randomUUID());

        List<OrderStatus> completedList = List.of(completed, duplicated);

        when(orderStatusRepository.findByStatus(OrderStatusTestConstants.COMPLETED)).thenReturn(completedList);

        Exception exception = Assertions.assertThrows(ConfigurationException.class, () -> {
            orderService.updateNextOrder();
        });

        String actualMessage = exception.getMessage();

        Assertions.assertTrue(actualMessage.contains(WRONG_CONFIGURATION_MESSAGE));
    }

    /**
     * Test update next order with empty orders list.
     *
     * @throws ConfigurationException the configuration exception (missing database configuration values)
     * @author Gianluca Sabato
     */
    @Test
    public void testUpdateNextOrderNotFound1() throws ConfigurationException {
        OrderStatus received = new OrderStatus();
        received.setStatus(OrderStatusTestConstants.RECEVIED);
        received.setOrderStatusId(OrderStatusTestConstants.RECEVIED_ID);
        List<OrderStatus> receivedList = List.of(received);

        when(orderStatusRepository.findByStatus(OrderStatusTestConstants.RECEVIED)).thenReturn(receivedList);

        OrderStatus processing = new OrderStatus();
        processing.setStatus(OrderStatusTestConstants.PROCESSING);
        processing.setOrderStatusId(OrderStatusTestConstants.PROCESSING_ID);
        List<OrderStatus> processingList = List.of(processing);

        when(orderStatusRepository.findByStatus(OrderStatusTestConstants.PROCESSING)).thenReturn(processingList);

        OrderStatus completed = new OrderStatus();
        completed.setStatus(OrderStatusTestConstants.COMPLETED);
        completed.setOrderStatusId(OrderStatusTestConstants.COMPLETED_ID);
        List<OrderStatus> completedList = List.of(completed);

        when(orderStatusRepository.findByStatus(OrderStatusTestConstants.COMPLETED)).thenReturn(completedList);

        when(orderRepository.findByOrderStatusOrderByCreatedDateAsc(received)).thenReturn(new ArrayList<>());

        Optional<OrderDTO> nextOrderOptional = orderService.updateNextOrder();

        Assertions.assertTrue(nextOrderOptional.isEmpty());
    }

    /**
     * Test update next order with null orders list.
     *
     * @throws ConfigurationException the configuration exception (missing database configuration values)
     * @author Gianluca Sabato
     */
    @Test
    public void testUpdateNextOrderNotFound2() throws ConfigurationException {
        OrderStatus received = new OrderStatus();
        received.setStatus(OrderStatusTestConstants.RECEVIED);
        received.setOrderStatusId(OrderStatusTestConstants.RECEVIED_ID);
        List<OrderStatus> receivedList = List.of(received);

        when(orderStatusRepository.findByStatus(OrderStatusTestConstants.RECEVIED)).thenReturn(receivedList);

        OrderStatus processing = new OrderStatus();
        processing.setStatus(OrderStatusTestConstants.PROCESSING);
        processing.setOrderStatusId(OrderStatusTestConstants.PROCESSING_ID);
        List<OrderStatus> processingList = List.of(processing);

        when(orderStatusRepository.findByStatus(OrderStatusTestConstants.PROCESSING)).thenReturn(processingList);

        OrderStatus completed = new OrderStatus();
        completed.setStatus(OrderStatusTestConstants.COMPLETED);
        completed.setOrderStatusId(OrderStatusTestConstants.COMPLETED_ID);
        List<OrderStatus> completedList = List.of(completed);

        when(orderStatusRepository.findByStatus(OrderStatusTestConstants.COMPLETED)).thenReturn(completedList);

        when(orderRepository.findByOrderStatusOrderByCreatedDateAsc(received)).thenReturn(null);

        Optional<OrderDTO> nextOrderOptional = orderService.updateNextOrder();

        Assertions.assertTrue(nextOrderOptional.isEmpty());
    }


    /**
     * Test get order.
     * @author Gianluca Sabato
     */
    @Test
    public void testGetOrder() {
        UUID orderId = UUID.randomUUID();
        List<String> pizzas = List.of("margherita", "diavola");

        OrderStatus received = new OrderStatus();
        received.setOrderStatusId(OrderStatusTestConstants.RECEVIED_ID);
        received.setStatus(OrderStatusTestConstants.RECEVIED);

        Order order = new Order();
        order.setOrderId(orderId);
        order.setPizzas(pizzas);
        order.setOrderStatus(received);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        Optional<OrderDTO> optionalOrderDTO = orderService.getOrder(orderId);

        Assertions.assertTrue(optionalOrderDTO.isPresent());

        OrderDTO foundOrderDTO = optionalOrderDTO.get();

        Assertions.assertNotNull(foundOrderDTO.getOrderId());
        Assertions.assertEquals(foundOrderDTO.getOrderId(), orderId);
        Assertions.assertNotNull(foundOrderDTO.getPizzas());
        Assertions.assertFalse(foundOrderDTO.getPizzas().isEmpty());
        Assertions.assertIterableEquals(foundOrderDTO.getPizzas(), order.getPizzas());
    }

    /**
     * Test get order but no order is persisted on the database.
     * @author Gianluca Sabato
     */
    @Test
    public void testGetOrderNotFound() {
        UUID orderId = UUID.randomUUID();

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        Optional<OrderDTO> optionalOrderDTO = orderService.getOrder(orderId);

        Assertions.assertTrue(optionalOrderDTO.isEmpty());
    }

    /**
     * Test create order.
     *
     * @throws ConfigurationException the configuration exception (missing database configuration values)
     * @author Gianluca Sabato
     */
    @Test
    public void testCreateOrder() throws ConfigurationException {
        OrderStatus received = new OrderStatus();
        received.setStatus(OrderStatusTestConstants.RECEVIED);
        received.setOrderStatusId(OrderStatusTestConstants.RECEVIED_ID);
        List<OrderStatus> receivedList = List.of(received);

        when(orderStatusRepository.findByStatus(OrderStatusTestConstants.RECEVIED)).thenReturn(receivedList);

        List<String> pizzas = List.of("Margherita", "Diavola", "Tirolese");
        OrderDTO orderDTO = new OrderDTO(pizzas);
        Order order = new Order();
        order.setOrderId(UUID.randomUUID());
        order.setOrderStatus(received);
        order.setPizzas(pizzas);

        when(orderRepository.save(Mockito.any(Order.class))).thenReturn(order);

        OrderDTO createdOrderDTO = orderService.createOrder(orderDTO);

        Assertions.assertNotNull(createdOrderDTO.getOrderId());
        Assertions.assertNotNull(createdOrderDTO.getPizzas());
        Assertions.assertFalse(createdOrderDTO.getPizzas().isEmpty());
        Assertions.assertIterableEquals(orderDTO.getPizzas(), createdOrderDTO.getPizzas());
    }

    /**
     * Test create order with empty order statuses list (RECEIVED).
     * @author Gianluca Sabato
     */
    @Test
    public void testCreateOrderMissingStatusConfiguration() {
        when(orderStatusRepository.findByStatus(OrderStatusTestConstants.RECEVIED)).thenReturn(new ArrayList<>());

        OrderDTO orderDTO = new OrderDTO(List.of("Margherita", "Diavola", "Tirolese"));

        Exception exception = Assertions.assertThrows(ConfigurationException.class, () -> {
            orderService.createOrder(orderDTO);
        });

        String actualMessage = exception.getMessage();

        Assertions.assertTrue(actualMessage.contains(WRONG_CONFIGURATION_MESSAGE));
    }

    /**
     * Test create order with null order statuses list (RECEIVED).
     * @author Gianluca Sabato
     */
    @Test
    public void testCreateOrderNullStatusConfiguration() {
        when(orderStatusRepository.findByStatus(OrderStatusTestConstants.RECEVIED)).thenReturn(null);

        OrderDTO orderDTO = new OrderDTO(List.of("Margherita", "Diavola", "Tirolese"));

        Exception exception = Assertions.assertThrows(ConfigurationException.class, () -> {
            orderService.createOrder(orderDTO);
        });

        String actualMessage = exception.getMessage();

        Assertions.assertTrue(actualMessage.contains(WRONG_CONFIGURATION_MESSAGE));
    }

    /**
     * Test create order with wrong status configuration.
     * @author Gianluca Sabato
     */
    @Test
    public void testCreateOrderWrongStatusConfiguration() {
        OrderStatus received = new OrderStatus();
        received.setStatus(OrderStatusTestConstants.RECEVIED);
        received.setOrderStatusId(OrderStatusTestConstants.RECEVIED_ID);

        OrderStatus duplicated = new OrderStatus();
        duplicated.setStatus(OrderStatusTestConstants.RECEVIED);
        duplicated.setOrderStatusId(UUID.randomUUID());

        List<OrderStatus> receivedList = List.of(received, duplicated);

        when(orderStatusRepository.findByStatus(OrderStatusTestConstants.RECEVIED)).thenReturn(receivedList);

        OrderDTO orderDTO = new OrderDTO(List.of("Margherita", "Diavola", "Tirolese"));

        Exception exception = Assertions.assertThrows(ConfigurationException.class, () -> {
            orderService.createOrder(orderDTO);
        });

        String actualMessage = exception.getMessage();

        Assertions.assertTrue(actualMessage.contains(WRONG_CONFIGURATION_MESSAGE));
    }

    /**
     * Test save order.
     *
     * @throws ConfigurationException the configuration exception (missing database configuration values)
     * @author Gianluca Sabato
     */
    @Test
    public void testSaveOrder() throws ConfigurationException {
        OrderStatus received = new OrderStatus();
        received.setStatus(OrderStatusTestConstants.RECEVIED);
        received.setOrderStatusId(OrderStatusTestConstants.RECEVIED_ID);

        Order order = new Order();
        order.setOrderId(UUID.randomUUID());
        order.setPizzas(List.of("margherita"));
        order.setOrderStatus(received);

        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));

        OrderStatus processing = new OrderStatus();
        processing.setStatus(OrderStatusTestConstants.PROCESSING);
        processing.setOrderStatusId(OrderStatusTestConstants.PROCESSING_ID);

        when(orderStatusRepository.findById(OrderStatusTestConstants.PROCESSING_ID)).thenReturn(
                Optional.of(processing));

        Order savedOrder = new Order();
        savedOrder.setOrderId(order.getOrderId());
        savedOrder.setPizzas(List.of("margherita"));
        savedOrder.setOrderStatus(processing);

        when(orderRepository.save(savedOrder)).thenReturn(savedOrder);

        OrderDTO orderDTO = OrderDTOFactory.getOrderDTO(order);

        OrderStatusDTO orderStatusDTO = new OrderStatusDTO(OrderStatusTestConstants.PROCESSING_ID);
        orderStatusDTO.setOrderStatus(OrderStatusTestConstants.PROCESSING);

        orderService.saveOrder(orderDTO, orderStatusDTO);
    }

    /**
     * Test update order when the order is not persisted inside the database.
     *
     * @throws ConfigurationException the configuration exception (missing database configuration values)
     * @author Gianluca Sabato
     */
    @Test
    public void testSaveOrderNotFound() throws ConfigurationException {
        OrderStatus received = new OrderStatus();
        received.setStatus(OrderStatusTestConstants.RECEVIED);
        received.setOrderStatusId(OrderStatusTestConstants.RECEVIED_ID);

        Order order = new Order();
        order.setOrderId(UUID.randomUUID());
        order.setPizzas(List.of("margherita"));
        order.setOrderStatus(received);

        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.empty());

        OrderDTO orderDTO = OrderDTOFactory.getOrderDTO(order);

        OrderStatusDTO orderStatusDTO = new OrderStatusDTO(OrderStatusTestConstants.PROCESSING_ID);
        orderStatusDTO.setOrderStatus(OrderStatusTestConstants.PROCESSING);

        orderService.saveOrder(orderDTO, orderStatusDTO);
    }

    /**
     * Test save order with missing status configuration.
     * @author Gianluca Sabato
     */
    @Test
    public void testSaveOrderNotFoundMissingStatusConfiguration() {
        OrderStatus received = new OrderStatus();
        received.setStatus(OrderStatusTestConstants.RECEVIED);
        received.setOrderStatusId(OrderStatusTestConstants.RECEVIED_ID);

        Order order = new Order();
        order.setOrderId(UUID.randomUUID());
        order.setPizzas(List.of("margherita"));
        order.setOrderStatus(received);

        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));

        OrderDTO orderDTO = OrderDTOFactory.getOrderDTO(order);

        OrderStatusDTO orderStatusDTO = new OrderStatusDTO(OrderStatusTestConstants.PROCESSING_ID);
        orderStatusDTO.setOrderStatus(OrderStatusTestConstants.PROCESSING);

        Exception exception = Assertions.assertThrows(ConfigurationException.class, () -> {
            orderService.saveOrder(orderDTO, orderStatusDTO);
        });

        String actualMessage = exception.getMessage();

        Assertions.assertTrue(actualMessage.contains(WRONG_CONFIGURATION_MESSAGE));
    }

    // OrderStatusService

    /**
     * Test get order statuses.
     * @author Gianluca Sabato
     */
    @Test
    public void testGetOrderStatuses() {
        OrderStatus received = new OrderStatus();
        received.setStatus(OrderStatusTestConstants.RECEVIED);
        received.setOrderStatusId(OrderStatusTestConstants.RECEVIED_ID);

        OrderStatus processing = new OrderStatus();
        processing.setStatus(OrderStatusTestConstants.PROCESSING);
        processing.setOrderStatusId(OrderStatusTestConstants.PROCESSING_ID);

        OrderStatus completed = new OrderStatus();
        completed.setStatus(OrderStatusTestConstants.COMPLETED);
        completed.setOrderStatusId(OrderStatusTestConstants.COMPLETED_ID);

        OrderStatus cancelled = new OrderStatus();
        cancelled.setStatus(OrderStatusTestConstants.CANCELLED);
        cancelled.setOrderStatusId(OrderStatusTestConstants.CANCELLED_ID);

        List<OrderStatus> orderStatuses = List.of(received, processing, completed, cancelled);

        when(orderStatusRepository.findAll()).thenReturn(orderStatuses);

        List<OrderStatusDTO> orderStatusDTOS = orderStatusService.getOrderStatuses();

        Assertions.assertNotNull(orderStatusDTOS);
        Assertions.assertFalse(orderStatusDTOS.isEmpty());
        Assertions.assertEquals(orderStatuses.size(), orderStatusDTOS.size());
    }

    /**
     * Test get order status.
     * @author Gianluca Sabato
     */
    @Test
    public void testGetOrderStatus() {
        OrderStatus received = new OrderStatus();
        received.setStatus(OrderStatusTestConstants.RECEVIED);
        received.setOrderStatusId(OrderStatusTestConstants.RECEVIED_ID);

        when(orderStatusRepository.findById(received.getOrderStatusId())).thenReturn(Optional.of(received));

        Optional<OrderStatusDTO> optionalOrderStatusDTO = orderStatusService.getOrderStatus(
                received.getOrderStatusId());

        Assertions.assertTrue(optionalOrderStatusDTO.isPresent());

        OrderStatusDTO orderStatusDTO = optionalOrderStatusDTO.get();

        Assertions.assertNotNull(orderStatusDTO);
        Assertions.assertEquals(received.getOrderStatusId(), orderStatusDTO.getOrderStatusId());
        Assertions.assertEquals(received.getStatus(), orderStatusDTO.getOrderStatus());
    }

    /**
     * Test get order status with missing database order statuses configuration.
     * @author Gianluca Sabato
     */
    @Test
    public void testGetOrderStatusNotFound() {
        when(orderStatusRepository.findById(OrderStatusTestConstants.RECEVIED_ID)).thenReturn(Optional.empty());

        Optional<OrderStatusDTO> optionalOrderStatusDTO = orderStatusService.getOrderStatus(
                OrderStatusTestConstants.RECEVIED_ID);

        Assertions.assertTrue(optionalOrderStatusDTO.isEmpty());
    }

    /**
     * Test get order status for order.
     * @author Gianluca Sabato
     */
    @Test
    public void testGetOrderStatusForOrder() {
        UUID orderId = UUID.randomUUID();
        List<String> pizzas = List.of("margherita", "diavola");

        OrderStatus received = new OrderStatus();
        received.setOrderStatusId(OrderStatusTestConstants.RECEVIED_ID);
        received.setStatus(OrderStatusTestConstants.RECEVIED);

        Order order = new Order();
        order.setOrderId(orderId);
        order.setPizzas(pizzas);
        order.setOrderStatus(received);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        Optional<OrderStatusDTO> optionalOrderStatusDTO = orderStatusService.getOrderStatusForOrderId(orderId);

        Assertions.assertTrue(optionalOrderStatusDTO.isPresent());

        OrderStatusDTO orderStatusDTO = optionalOrderStatusDTO.get();

        Assertions.assertNotNull(orderStatusDTO);
        Assertions.assertEquals(received.getOrderStatusId(), orderStatusDTO.getOrderStatusId());
        Assertions.assertEquals(received.getStatus(), orderStatusDTO.getOrderStatus());
    }

    /**
     * Test get order status for order if the order is not found.
     * @author Gianluca Sabato
     */
    @Test
    public void testGetOrderStatusForOrderNotFound1() {
        UUID orderId = UUID.randomUUID();

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        orderStatusService.getOrderStatusForOrderId(orderId);
    }

    /**
     * Test get order status for order with missing order statuses database configuration.
     * @author Gianluca Sabato
     */
    @Test
    public void testGetOrderStatusForOrderNotFound2() {
        UUID orderId = UUID.randomUUID();
        List<String> pizzas = List.of("margherita", "diavola");

        Order order = new Order();
        order.setOrderId(orderId);
        order.setPizzas(pizzas);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        orderStatusService.getOrderStatusForOrderId(orderId);
    }

    // Exceptions

    /**
     * Test exception handler.
     * @author Gianluca Sabato
     */
    @Test
    public void testExceptionHandler() {
        ConfigurationException configurationException = new ConfigurationException(WRONG_CONFIGURATION_MESSAGE);

        servletRequest.setServerName("localhost");
        servletRequest.setRequestURI("/orders");
        WebRequest webRequest = new ServletWebRequest(servletRequest);
        ResponseEntity<Object> responseEntity = handler.handleConfigurationException(configurationException,
                webRequest);

        Assertions.assertNotNull(responseEntity);

        Object body = responseEntity.getBody();

        Assertions.assertNotNull(body);
        Assertions.assertInstanceOf(ErrorDTO.class, body);

        ErrorDTO errorDTO = (ErrorDTO) body;

        Assertions.assertEquals(errorDTO.getDetail(), WRONG_CONFIGURATION_MESSAGE);
        Assertions.assertEquals(errorDTO.getStatus(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        Assertions.assertEquals(errorDTO.getTitle(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        Assertions.assertEquals(errorDTO.getType(), "about:blank");
        Assertions.assertTrue(StringUtils.contains(errorDTO.getInstance(), "/orders"));
    }
}
