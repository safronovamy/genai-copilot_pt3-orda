package com.example.orders.spec;

// Implement JPA Specifications for Order filtering.
// Create a utility class OrderSpecifications with static methods that return Specification<Order>.
//
// Filters (all optional):
// - status: OrderStatus equals
// - minAmount: Order.amount >= minAmount
// - maxAmount: Order.amount <= maxAmount
// - dateFrom: Order.createdAt >= start of dateFrom (inclusive)
// - dateTo: Order.createdAt <= end of dateTo (inclusive)
//
// Notes / requirements:
// - Use Spring Data JPA Specification<Order>.
// - Only add predicates when the corresponding parameter is not null.
// - Use CriteriaBuilder with type-safe paths: root.get("amount"), root.get("status"), root.get("createdAt").
// - dateFrom/dateTo are passed as LocalDate (ISO yyyy-MM-dd from query params); convert to Instant boundaries in UTC:
//   dateFrom.atStartOfDay(ZoneOffset.UTC).toInstant()
//   dateTo.plusDays(1).atStartOfDay(ZoneOffset.UTC).minusNanos(1).toInstant()
// - Provide one method: build(status, minAmount, maxAmount, dateFrom, dateTo) that returns a single Specification<Order>
//   combining all filters with and().
// - Ensure the class cannot be instantiated (private constructor).
import com.example.orders.model.Order;
import com.example.orders.model.OrderStatus;
import org.springframework.data.jpa.domain.Specification;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.Instant;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
public class OrderSpecifications {

    private OrderSpecifications() {
        // Prevent instantiation
    }

    public static Specification<Order> build(
            OrderStatus status,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            LocalDate dateFrom,
            LocalDate dateTo) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }
            if (minAmount != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("amount"), minAmount));
            }
            if (maxAmount != null) {
                predicates.add(
                        criteriaBuilder.lessThanOrEqualTo(root.get("amount"), maxAmount)
                );
            }
            if (dateFrom != null) {
                Instant fromInstant = dateFrom.atStartOfDay(ZoneOffset.UTC).toInstant();
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), fromInstant));
            }
            if (dateTo != null) {
                Instant toInstant = dateTo.plusDays(1).atStartOfDay(ZoneOffset.UTC).minusNanos(1).toInstant();
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), toInstant));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
