package it.sabato.pizzeria.controller;

import it.sabato.pizzeria.dto.OrderDTO;
import it.sabato.pizzeria.dto.OrderStatusDTO;
import it.sabato.pizzeria.service.OrderService;
import it.sabato.pizzeria.service.OrderStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.naming.ConfigurationException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


/**
 * RestController to manage all the REST APIs related to orders.
 *
 * @author Gianluca Sabato
 */
@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final OrderStatusService orderStatusService;

    //Order

    /**
     * Gets orders.
     *
     * @return the orders
     * @author Gianluca Sabato
     */
    @GetMapping("/orders")
    public CollectionModel<OrderDTO> getOrders() {
        List<OrderDTO> orderDTOS = orderService.getOrders();
        orderDTOS = orderDTOS.stream().peek(o -> {
            final Link selfLink = linkTo(OrderController.class).slash(o.getOrderId()).withSelfRel();
            o.add(selfLink);
        }).toList();

        return CollectionModel.of(orderDTOS);
    }

    /**
     * Retrieve the next order to be processed.
     *
     * @return the next order
     * @throws ConfigurationException the configuration exception (missing database configuration values)
     * @author Gianluca Sabato
     */
    @GetMapping("/orders/next")
    public EntityModel<OrderDTO> getNextOrder() throws ConfigurationException {
        Optional<OrderDTO> nextOrderOptional = orderService.getNextOrder();

        if (nextOrderOptional.isPresent()) {
            OrderDTO nextOrder = nextOrderOptional.get();

            return EntityModel.of(nextOrder);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "next order not found");
        }
    }

    /**
     * Update the status of the current order (from PROCESSING to CLOSED) and then retrieve the next order to be
     * processed updating its status (from RECEIVED to PROCESSING).
     *
     * @return the next order
     * @throws ConfigurationException the configuration exception (missing database configuration values)
     * @author Gianluca Sabato
     */
    @PutMapping("/orders/next")
    public EntityModel<OrderDTO> putNextOrder() throws ConfigurationException {
        Optional<OrderDTO> nextOrderOptional = orderService.updateNextOrder();

        if (nextOrderOptional.isPresent()) {
            OrderDTO nextOrder = nextOrderOptional.get();

            return EntityModel.of(nextOrder);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "next order not found");
        }
    }

    /**
     * Gets order.
     *
     * @param id the order id
     * @return the order
     * @author Gianluca Sabato
     */
    @GetMapping("/orders/{id}")
    public EntityModel<OrderDTO> getOrder(@PathVariable UUID id) {
        Optional<OrderDTO> optionalOrderDTO = orderService.getOrder(id);

        if (optionalOrderDTO.isPresent()) {
            OrderDTO orderDTO = optionalOrderDTO.get();
            final Link selfLink = linkTo(methodOn(OrderController.class).getOrder(id)).withSelfRel();
            orderDTO.add(selfLink);

            final Link orderLink = linkTo(methodOn(OrderController.class).getOrderStatusForOrder(id)).withRel(
                    "orderStatus");
            orderDTO.add(orderLink);

            return EntityModel.of(orderDTO);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "order not found");
        }
    }

    /**
     * Gets order status for order.
     *
     * @param id the order id
     * @return the order status for order
     * @author Gianluca Sabato
     */
    @GetMapping("/orders/{id}/orderStatus")
    public EntityModel<OrderStatusDTO> getOrderStatusForOrder(@PathVariable UUID id) {
        Optional<OrderStatusDTO> optionalOrderStatusDTO = orderStatusService.getOrderStatusForOrderId(id);
        OrderStatusDTO orderStatusDTO;

        if (optionalOrderStatusDTO.isPresent()) {
            orderStatusDTO = optionalOrderStatusDTO.get();

            final Link selfLink = linkTo(methodOn(OrderController.class).getOrderStatusForOrder(id)).withSelfRel();
            orderStatusDTO.add(selfLink);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "order status not found");
        }

        return EntityModel.of(orderStatusDTO);
    }

    /**
     * Updates the order status for a specific order.
     *
     * @param id                    the order id
     * @param orderStatusDTORequest the order status dto request
     * @return the order status
     * @throws ConfigurationException the configuration exception (missing database configuration values)
     * @author Gianluca Sabato
     */
    @PutMapping("/orders/{id}/orderStatus")
    public EntityModel<OrderStatusDTO> putOrderStatusForOrder(@PathVariable UUID id,
                                                              @RequestBody @Valid OrderStatusDTO orderStatusDTORequest)
            throws ConfigurationException {
        Optional<OrderDTO> optionalOrderDTO = orderService.getOrder(id);

        if (optionalOrderDTO.isPresent()) {
            OrderDTO orderDTO = optionalOrderDTO.get();

            Optional<OrderStatusDTO> optionalOrderStatusDTO = orderStatusService.getOrderStatus(
                    orderStatusDTORequest.getOrderStatusId());

            if (optionalOrderStatusDTO.isPresent()) {
                OrderStatusDTO orderStatusDTO = optionalOrderStatusDTO.get();

                orderService.saveOrder(orderDTO, orderStatusDTO);
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "order status not found");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "order not found");
        }

        return EntityModel.of(orderStatusDTORequest);
    }

    /**
     * Create a new order.
     *
     * @param orderDTORequest the order dto request
     * @return the new order
     * @throws ConfigurationException the configuration exception (missing database configuration values)
     * @author Gianluca Sabato
     */
    @PostMapping("/orders")
    public ResponseEntity<OrderDTO> postOrder(@RequestBody @Valid OrderDTO orderDTORequest)
            throws ConfigurationException {
        OrderDTO orderDTO = orderService.createOrder(orderDTORequest);

        final Link selfLink = linkTo(methodOn(OrderController.class).getOrder(orderDTO.getOrderId())).withSelfRel();
        orderDTO.add(selfLink);

        return ResponseEntity.created(orderDTO.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(orderDTO);
    }
}
