package com.hsl.prompt_be.repositories.specifications;

import com.hsl.prompt_be.entities.models.Order;
import org.springframework.data.jpa.domain.Specification;

import java.util.Map;

public class OrderSpecification {

    public static Specification<Order> byDynamicCriteria(Map<String, String> criteria) {
        return (root, query, criteriaBuilder) -> {
            // Start with a "true" predicate (no filtering)
            var predicate = criteriaBuilder.conjunction();

            // Add 'LIKE' conditions for each column-value pair
            for (Map.Entry<String, String> entry : criteria.entrySet()) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.like(root.get(entry.getKey()), "%" + entry.getValue() + "%")
                );
            }

            return predicate;
        };
    }
}
