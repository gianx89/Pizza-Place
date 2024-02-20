package it.sabato.pizzeria.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.util.UUID;

/**
 * The type Order status dto.
 * @author Gianluca Sabato
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class OrderStatusDTO extends RepresentationModel<OrderStatusDTO> {
    @NotNull
    private final UUID orderStatusId;
    private String orderStatus;
}
