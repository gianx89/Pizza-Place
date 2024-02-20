package it.sabato.pizzeria.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;
import java.util.UUID;

/**
 * The type Order dto.
 * @author Gianluca Sabato
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class OrderDTO extends RepresentationModel<OrderDTO> {

    private UUID orderId;
    @NotEmpty(message = "Input pizza list cannot be empty.")
    private final List<String> pizzas;
}
