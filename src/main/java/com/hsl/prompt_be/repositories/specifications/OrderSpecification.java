package com.hsl.prompt_be.repositories.specifications;

import com.hsl.prompt_be.entities.models.Order;
import com.hsl.prompt_be.entities.requests.SpecificationRequest;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class OrderSpecification {

    public static Specification<Order> byDynamicCriteria(List<SpecificationRequest> criterias) {
        return (root, query, criteriaBuilder) -> {
            // Start with a "true" predicate (no filtering)
            var predicate = criteriaBuilder.conjunction();

            // Add 'LIKE' conditions for each column-value pair
            for (SpecificationRequest entry : criterias) {

                if (entry.getOperator().toLowerCase().equals("like")) {
                    predicate = criteriaBuilder.and(
                            predicate,
                            criteriaBuilder.like(root.get(entry.getTag()), "%" + entry.getValue() + "%")
                    );
                }
                else if (entry.getOperator().toLowerCase().equals("equal"))  {
                    if (entry.getTag().endsWith("Id")) {
                        entry.setValue(UUID.fromString((String) entry.getValue()));
                    }
                    predicate = criteriaBuilder.and(
                            predicate,
                            criteriaBuilder.equal(root.get(entry.getTag()), entry.getValue())
                    );
                }
                else {
                    throw new UnsupportedOperationException("Operation " + entry.getOperator() + " not supported");
                }
            }

            return predicate;
        };
    }
}
