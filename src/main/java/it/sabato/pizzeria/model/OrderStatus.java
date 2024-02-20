package it.sabato.pizzeria.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static jakarta.persistence.CascadeType.*;

/**
 * The type Order status.
 * @author Gianluca Sabato
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "order_statuses")
public class OrderStatus extends RepresentationModel<OrderStatus> {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID orderStatusId;
    private String status;
    @OneToMany(cascade = {MERGE, REFRESH, DETACH})
    @JoinColumn(name = "order_status_order_status_id")
    private List<Order> orders = new ArrayList<>();
}
