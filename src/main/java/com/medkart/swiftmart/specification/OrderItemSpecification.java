package com.medkart.swiftmart.specification;

import com.medkart.swiftmart.entity.OrderItem;
import com.medkart.swiftmart.enums.OrderStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class OrderItemSpecification {

    // To filter order items by status
    public static Specification<OrderItem> hasStatus(OrderStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status != null ? cb.equal(root.get("status"), status) : null);
    }

    // Specification to filter orders by date ranges
    public static Specification<OrderItem> createdBetween(LocalDateTime start, LocalDateTime end) {
        return (root, query, cb) -> {
            if( start != null && end != null)
                return cb.between(root.get("createdAt"), start, end);
            else if(start != null)
                return cb.greaterThanOrEqualTo(root.get("createdAt"), start);
            else  if (end != null)
                return cb.lessThanOrEqualTo(root.get("createdAt"), end);
            else
                return null;
        };
    }

    public static Specification<OrderItem> hasItemId(Long itemId)
        { return (root, query, cb) -> cb.equal(root.get("id"), itemId); }

}
