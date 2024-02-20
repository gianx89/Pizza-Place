package it.sabato.pizzeria.controller;

import it.sabato.pizzeria.dto.OrderStatusDTO;
import it.sabato.pizzeria.service.OrderStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * The Order status controller.
 */
@RestController
@RequiredArgsConstructor
public class OrderStatusController {
    private final OrderStatusService orderStatusService;

    /**
     * Gets order statuses
     *
     * @return the order statuses
     * @author Gianluca Sabato
     */
    @GetMapping("/orderStatuses")
    public CollectionModel<OrderStatusDTO> getOrders() {
        List<OrderStatusDTO> orderStatusDTOS = orderStatusService.getOrderStatuses();
        orderStatusDTOS = orderStatusDTOS.stream().peek(os -> {
            final Link selfLink = linkTo(OrderStatusController.class).slash(os.getOrderStatusId()).withSelfRel();
            os.add(selfLink);
        }).toList();

        return CollectionModel.of(orderStatusDTOS);
    }

    /**
     * Gets order status.
     *
     * @param id the order status id
     * @return the order status
     * @author Gianluca Sabato
     */
    @GetMapping("/orderStatuses/{id}")
    public EntityModel<OrderStatusDTO> getOrderStatus(@PathVariable UUID id) {
        Optional<OrderStatusDTO> optionalOrderStatusDTO = orderStatusService.getOrderStatus(id);

        if (optionalOrderStatusDTO.isPresent()) {
            OrderStatusDTO orderStatusDTO = optionalOrderStatusDTO.get();
            final Link selfLink = linkTo(methodOn(OrderStatusController.class).getOrderStatus(id)).withSelfRel();
            orderStatusDTO.add(selfLink);

            return EntityModel.of(orderStatusDTO);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "order status not found");
        }
    }
}
