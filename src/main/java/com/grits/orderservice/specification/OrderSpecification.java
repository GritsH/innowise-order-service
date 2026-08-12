package com.grits.orderservice.specification;

import com.grits.orderservice.entity.Order;
import com.grits.orderservice.entity.status.OrderStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class OrderSpecification {

    public static Specification<Order> notDeleted() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isFalse(root.get("deleted"));
    }

    public static Specification<Order> createdFrom(LocalDateTime from) {
        return (root, query, criteriaBuilder) -> from == null
                                                 ? criteriaBuilder.conjunction()
                                                 : criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), from);
    }

    public static Specification<Order> createdTo(LocalDateTime to) {
        return (root, query, ccriteriaBuilder) -> to == null
                                                  ? ccriteriaBuilder.conjunction()
                                                  : ccriteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), to);
    }

    public static Specification<Order> hasStatuses(List<OrderStatus> statuses) {
        return (root, query, criteriaBuilder) -> statuses == null || statuses.isEmpty()
                                                 ? criteriaBuilder.conjunction()
                                                 : root.get("status").in(statuses);
    }

    public static Specification<Order> hasUserId(UUID userId) {
        return (root, query, cb) -> userId == null
                                    ? cb.conjunction()
                                    : cb.equal(root.get("userId"), userId);
    }
}