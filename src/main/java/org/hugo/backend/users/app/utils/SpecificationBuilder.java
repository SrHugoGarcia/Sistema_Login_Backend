package org.hugo.backend.users.app.utils;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
//campo:operador:valor
public class SpecificationBuilder<T> {
    public Specification<T> buildSpecification(List<String> filters) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (filters != null && !filters.isEmpty()) {
                for (String filter : filters) {
                    if (filter.contains(":")) {
                        String[] filterParts = filter.split(":");
                        if (filterParts.length == 3) {
                            String fieldName = filterParts[0];
                            String operator = filterParts[1];
                            String value = filterParts[2];

                            // Lógica para construir predicados con diferentes operadores.
                            Predicate predicate;

                            switch (operator) {
                                case "eq":
                                    predicate = criteriaBuilder.equal(root.get(fieldName), value);
                                    break;
                                case "ne":
                                    predicate = criteriaBuilder.notEqual(root.get(fieldName), value);
                                    break;
                                case "gt":
                                    predicate = criteriaBuilder.greaterThan(root.get(fieldName), value);
                                    break;
                                case "lt":
                                    predicate = criteriaBuilder.lessThan(root.get(fieldName), value);
                                    break;
                                case "ge":
                                    predicate = criteriaBuilder.greaterThanOrEqualTo(root.get(fieldName), value);
                                    break;
                                case "le":
                                    predicate = criteriaBuilder.lessThanOrEqualTo(root.get(fieldName), value);
                                    break;
                                case "like":
                                    predicate = criteriaBuilder.like(root.get(fieldName), "%" + value + "%");
                                    break;
                                default:
                                    // Operador no válido, puedes manejarlo según tus necesidades.
                                    throw new IllegalArgumentException("Operador no válido: " + operator);
                            }

                            predicates.add(predicate);
                        }
                    }
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
