package it.sabato.pizzeria;

import it.sabato.pizzeria.config.OrderStatusTestConstants;
import it.sabato.pizzeria.config.PizzaPlaceDockerTestConf;
import it.sabato.pizzeria.dto.OrderDTO;
import it.sabato.pizzeria.dto.OrderStatusDTO;
import it.sabato.pizzeria.service.OrderService;
import it.sabato.pizzeria.service.OrderStatusService;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static it.sabato.pizzeria.config.IntegrationTestsQueries.*;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

/**
 * The type Pizza place integration tests.
 * @author Gianluca Sabato
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PizzaPlaceIntegrationTests extends PizzaPlaceDockerTestConf {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderStatusService orderStatusService;
    @Autowired
    private TestRestTemplate restTemplate;
    @LocalServerPort
    private int port;

    /**
     * Test get orders.
     * @author Gianluca Sabato
     */
    @Test
    public void testGetOrders() {
        CollectionModel<OrderDTO> collectionModel = restTemplate.exchange("http://localhost:" + port + "/orders",
                HttpMethod.GET, null, new ParameterizedTypeReference<CollectionModel<OrderDTO>>() {
                }).getBody();

        Assertions.assertNotNull(collectionModel);
        Assertions.assertNotNull(collectionModel.getContent());
        Assertions.assertFalse(collectionModel.getContent().isEmpty());

        collectionModel.getContent().forEach(o -> {
            Assertions.assertNotNull(o.getOrderId());
            Assertions.assertNotNull(o.getPizzas());
            Assertions.assertFalse(o.getPizzas().isEmpty());
        });
    }

    /**
     * Test get next order.
     * @author Gianluca Sabato
     */
    @Test
    public void testGetNextOrder() {
        EntityModel<OrderDTO> entityModel = restTemplate.exchange("http://localhost:" + port + "/orders/next",
                HttpMethod.GET, null, new ParameterizedTypeReference<EntityModel<OrderDTO>>() {
                }).getBody();

        Assertions.assertNotNull(entityModel);
        Assertions.assertNotNull(entityModel.getContent());

        OrderDTO orderDTO = entityModel.getContent();

        Assertions.assertNotNull(orderDTO.getOrderId());
        Assertions.assertNotNull(orderDTO.getPizzas());
        Assertions.assertFalse(orderDTO.getPizzas().isEmpty());
    }

    /**
     * Test get next order with no order persisted inside the database.
     * @author Gianluca Sabato
     */
    @Test
    @Sql(statements = {DELETE_ORDERS}, executionPhase = BEFORE_TEST_METHOD)
    @Sql(statements = {INSERT_ORDERS}, executionPhase = AFTER_TEST_METHOD)
    public void testGetNextOrderNotFound() {
        ResponseEntity<?> responseEntity = restTemplate.getForEntity("http://localhost:" + port + "/orders/next",
                EntityModel.class);

        Assertions.assertNotNull(responseEntity);
        Assertions.assertNotNull(responseEntity.getStatusCode());
        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    /**
     * Test put next order.
     * @author Gianluca Sabato
     */
    @Test
    public void testPutNextOrder() {
        EntityModel<OrderDTO> entityModel = restTemplate.exchange("http://localhost:" + port + "/orders/next",
                HttpMethod.PUT, null, new ParameterizedTypeReference<EntityModel<OrderDTO>>() {
                }).getBody();

        Assertions.assertNotNull(entityModel);
        Assertions.assertNotNull(entityModel.getContent());

        OrderDTO orderDTO = entityModel.getContent();

        Assertions.assertNotNull(orderDTO.getOrderId());
        Assertions.assertNotNull(orderDTO.getPizzas());
        Assertions.assertFalse(orderDTO.getPizzas().isEmpty());
    }

    /**
     * Test put next order, no RECEIVED order available.
     * @author Gianluca Sabato
     */
    @Test
    @Sql(statements = {DELETE_ORDERS}, executionPhase = BEFORE_TEST_METHOD)
    @Sql(statements = {INSERT_ORDERS}, executionPhase = AFTER_TEST_METHOD)
    public void testPutNextOrderNotFound() {
        ResponseEntity<?> responseEntity = restTemplate.exchange("http://localhost:" + port + "/orders/next",
                HttpMethod.PUT, null, EntityModel.class);

        Assertions.assertNotNull(responseEntity);
        Assertions.assertNotNull(responseEntity.getStatusCode());
        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    /**
     * Test get order.
     * @author Gianluca Sabato
     */
    @Test
    public void testGetOrder() {
        EntityModel<OrderDTO> entityModel = restTemplate.exchange(
                "http://localhost:" + port + "/orders/c2292f78-ca47-432b-b5cf-df0b0c739592", HttpMethod.GET, null,
                new ParameterizedTypeReference<EntityModel<OrderDTO>>() {
                }).getBody();

        Assertions.assertNotNull(entityModel);
        Assertions.assertNotNull(entityModel.getContent());

        OrderDTO orderDTO = entityModel.getContent();

        Assertions.assertNotNull(orderDTO.getOrderId());
        Assertions.assertNotNull(orderDTO.getPizzas());
        Assertions.assertFalse(orderDTO.getPizzas().isEmpty());
    }

    /**
     * Test get order when the order does not exist.
     * @author Gianluca Sabato
     */
    @Test
    public void testGetOrderNotFound() {
        ResponseEntity<?> responseEntity = restTemplate.getForEntity(
                "http://localhost:" + port + "/orders/c2292f78-ca47-432b-b5cf-df0b0c73959a", EntityModel.class);

        Assertions.assertNotNull(responseEntity);
        Assertions.assertNotNull(responseEntity.getStatusCode());
        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    /**
     * Test get order status for order.
     * @author Gianluca Sabato
     */
    @Test
    public void testGetOrderStatusForOrder() {
        EntityModel<OrderStatusDTO> entityModel = restTemplate.exchange(
                "http://localhost:" + port + "/orders/c2292f78-ca47-432b-b5cf-df0b0c739592/orderStatus", HttpMethod.GET,
                null, new ParameterizedTypeReference<EntityModel<OrderStatusDTO>>() {
                }).getBody();

        Assertions.assertNotNull(entityModel);
        Assertions.assertNotNull(entityModel.getContent());

        OrderStatusDTO orderStatusDTO = entityModel.getContent();

        Assertions.assertNotNull(orderStatusDTO.getOrderStatusId());
        Assertions.assertTrue(StringUtils.isNotBlank(orderStatusDTO.getOrderStatus()));
    }

    /**
     * Test get order status for order that doesn't exist.
     * @author Gianluca Sabato
     */
    @Test
    public void testGetOrderStatusForOrderNotFound() {
        ResponseEntity<?> responseEntity = restTemplate.getForEntity(
                "http://localhost:" + port + "/orders/c2292f78-ca47-432b-b5cf-df0b0c73959a/orderStatus",
                EntityModel.class);

        Assertions.assertNotNull(responseEntity);
        Assertions.assertNotNull(responseEntity.getStatusCode());
        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    /**
     * Test put order status for order.
     * @author Gianluca Sabato
     */
    @Test
    public void testPutOrderStatusForOrder() {
        OrderStatusDTO orderStatusDTORequest = new OrderStatusDTO(OrderStatusTestConstants.CANCELLED_ID);
        orderStatusDTORequest.setOrderStatus(OrderStatusTestConstants.CANCELLED);
        HttpEntity<OrderStatusDTO> request = new HttpEntity<>(orderStatusDTORequest);

        EntityModel<OrderStatusDTO> entityModel = restTemplate.exchange(
                "http://localhost:" + port + "/orders/c2292f78-ca47-432b-b5cf-df0b0c739592/orderStatus", HttpMethod.PUT,
                request, new ParameterizedTypeReference<EntityModel<OrderStatusDTO>>() {
                }).getBody();

        Assertions.assertNotNull(entityModel);
        Assertions.assertNotNull(entityModel.getContent());

        OrderStatusDTO orderStatusDTO = entityModel.getContent();

        Assertions.assertNotNull(orderStatusDTO.getOrderStatusId());
        Assertions.assertTrue(StringUtils.isNotBlank(orderStatusDTO.getOrderStatus()));
        Assertions.assertEquals(orderStatusDTORequest.getOrderStatusId(), orderStatusDTO.getOrderStatusId());
        Assertions.assertEquals(orderStatusDTORequest.getOrderStatus(), orderStatusDTO.getOrderStatus());
    }

    /**
     * Test put order status for order that doesn't exist.
     * @author Gianluca Sabato
     */
    @Test
    public void testPutOrderStatusForOrderNotFound1() {
        OrderStatusDTO orderStatusDTORequest = new OrderStatusDTO(OrderStatusTestConstants.CANCELLED_ID);
        orderStatusDTORequest.setOrderStatus(OrderStatusTestConstants.CANCELLED);
        HttpEntity<OrderStatusDTO> request = new HttpEntity<>(orderStatusDTORequest);

        ResponseEntity<?> responseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/orders/c2292f78-ca47-432b-b5cf-df0b0c73959a/orderStatus", HttpMethod.PUT,
                request, EntityModel.class);

        Assertions.assertNotNull(responseEntity);
        Assertions.assertNotNull(responseEntity.getStatusCode());
        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    /**
     * Test put order status for order when the order status is invalid.
     * @author Gianluca Sabato
     */
    @Test
    public void testPutOrderStatusForOrderNotFound2() {
        OrderStatusDTO orderStatusDTORequest = new OrderStatusDTO(UUID.randomUUID());
        orderStatusDTORequest.setOrderStatus(OrderStatusTestConstants.CANCELLED);
        HttpEntity<OrderStatusDTO> request = new HttpEntity<>(orderStatusDTORequest);

        ResponseEntity<?> responseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/orders/c2292f78-ca47-432b-b5cf-df0b0c739592/orderStatus", HttpMethod.PUT,
                request, EntityModel.class);

        Assertions.assertNotNull(responseEntity);
        Assertions.assertNotNull(responseEntity.getStatusCode());
        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    /**
     * Test post order.
     * @author Gianluca Sabato
     */
    @Test
    public void testPostOrder() {
        OrderDTO orderDTORequest = new OrderDTO(List.of("Margherita", "Marinara"));
        HttpEntity<OrderDTO> request = new HttpEntity<>(orderDTORequest);

        EntityModel<OrderDTO> entityModel = restTemplate.exchange("http://localhost:" + port + "/orders",
                HttpMethod.POST, request, new ParameterizedTypeReference<EntityModel<OrderDTO>>() {
                }).getBody();

        Assertions.assertNotNull(entityModel);
        Assertions.assertNotNull(entityModel.getContent());

        OrderDTO orderDTO = entityModel.getContent();

        Assertions.assertNotNull(orderDTO.getOrderId());
        Assertions.assertNotNull(orderDTO.getPizzas());
        Assertions.assertFalse(orderDTO.getPizzas().isEmpty());
        Assertions.assertIterableEquals(orderDTO.getPizzas(), orderDTORequest.getPizzas());
    }

    /**
     * Test get order statuses.
     * @author Gianluca Sabato
     */
    @Test
    public void testGetOrderStatuses() {
        CollectionModel<OrderStatusDTO> collectionModel = restTemplate.exchange(
                "http://localhost:" + port + "/orderStatuses", HttpMethod.GET, null,
                new ParameterizedTypeReference<CollectionModel<OrderStatusDTO>>() {
                }).getBody();

        Assertions.assertNotNull(collectionModel);
        Assertions.assertNotNull(collectionModel.getContent());
        Assertions.assertFalse(collectionModel.getContent().isEmpty());

        Collection<OrderStatusDTO> orderStatusDTOS = collectionModel.getContent();
        Set<UUID> ids = orderStatusDTOS.stream().map(OrderStatusDTO::getOrderStatusId).collect(Collectors.toSet());
        Set<String> statuses = orderStatusDTOS.stream().map(OrderStatusDTO::getOrderStatus).collect(Collectors.toSet());

        Set<UUID> referenceIds = Set.of(OrderStatusTestConstants.RECEVIED_ID, OrderStatusTestConstants.PROCESSING_ID,
                OrderStatusTestConstants.COMPLETED_ID, OrderStatusTestConstants.CANCELLED_ID);
        Set<String> referenceStatuses = Set.of(OrderStatusTestConstants.RECEVIED, OrderStatusTestConstants.PROCESSING,
                OrderStatusTestConstants.COMPLETED, OrderStatusTestConstants.CANCELLED);

        Assertions.assertEquals(ids, referenceIds);
        Assertions.assertEquals(statuses, referenceStatuses);
    }

    /**
     * Test get order status.
     * @author Gianluca Sabato
     */
    @Test
    public void testGetOrderStatus() {
        EntityModel<OrderStatusDTO> entityModel = restTemplate.exchange(
                "http://localhost:" + port + "/orderStatuses/df350171-e428-4d2c-a6c4-31123ef40ead", HttpMethod.GET,
                null, new ParameterizedTypeReference<EntityModel<OrderStatusDTO>>() {
                }).getBody();

        Assertions.assertNotNull(entityModel);
        Assertions.assertNotNull(entityModel.getContent());

        OrderStatusDTO orderStatusDTO = entityModel.getContent();

        Assertions.assertNotNull(orderStatusDTO.getOrderStatusId());
        Assertions.assertTrue(StringUtils.isNotBlank(orderStatusDTO.getOrderStatus()));
        Assertions.assertEquals(orderStatusDTO.getOrderStatusId(), OrderStatusTestConstants.CANCELLED_ID);
        Assertions.assertEquals(orderStatusDTO.getOrderStatus(), OrderStatusTestConstants.CANCELLED);
    }

    /**
     * Test get order status when the order status is invalid.
     * @author Gianluca Sabato
     */
    @Test
    public void testGetOrderStatusNotFound() {
        ResponseEntity<?> responseEntity = restTemplate.getForEntity(
                "http://localhost:" + port + "/orderStatuses/df350171-e428-4d2c-a6c4-31123ef40eab", EntityModel.class);

        Assertions.assertNotNull(responseEntity);
        Assertions.assertNotNull(responseEntity.getStatusCode());
        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.NOT_FOUND);
    }
}
