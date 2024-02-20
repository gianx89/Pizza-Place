package it.sabato.pizzeria.repositories;

import it.sabato.pizzeria.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * The interface Order status repository.
 * @author Gianluca Sabato
 */
@Repository
public interface OrderStatusRepository extends JpaRepository<OrderStatus, UUID>, JpaSpecificationExecutor<OrderStatus> {
    /**
     * Find by status.
     *
     * @param status the status
     * @return the filtered order statuses
     */
    List<OrderStatus> findByStatus(String status);
}
