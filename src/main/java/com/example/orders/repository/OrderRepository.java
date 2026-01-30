package com.example.orders.repository;

// Create a Spring Data JPA repository for Order.
// Requirements:
// - Must extend JpaRepository<Order, Long>.
// - Must also extend JpaSpecificationExecutor<Order> to support dynamic filtering.
// - No custom query methods are required for now.
// - Use package com.example.orders.repository and import com.example.orders.model.Order.
import com.example.orders.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    // No custom query methods required for now
}
