package it.sabato.pizzeria.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static jakarta.persistence.CascadeType.*;

/**
 * The type Order.
 * @author Gianluca Sabato
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "orders")
@EntityListeners(AuditingEntityListener.class)
public class Order extends RepresentationModel<Order> {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID orderId;
    private List<String> pizzas = new ArrayList<>();
    @ManyToOne(fetch = FetchType.EAGER, cascade = {MERGE, REMOVE, REFRESH, DETACH})
    @JoinColumn(name = "order_status_order_status_id")
    private OrderStatus orderStatus;
    @CreatedDate
    private LocalDateTime createdDate;
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
}
