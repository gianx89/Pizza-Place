package it.sabato.pizzeria.repositories;

import it.sabato.pizzeria.model.Order;
import it.sabato.pizzeria.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * The interface Order repository.
 * @author Gianluca Sabato
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, UUID>, JpaSpecificationExecutor<Order> {
    /**
     * Find by order status order and by created date with asc ordering.
     *
     * @param orderStatus the order status
     * @return the filtered orders
     * @author Gianluca Sabato
     */
    List<Order> findByOrderStatusOrderByCreatedDateAsc(OrderStatus orderStatus);
}
