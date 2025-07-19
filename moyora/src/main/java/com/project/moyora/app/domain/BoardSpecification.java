package com.project.moyora.app.domain;

import com.project.moyora.app.dto.BoardSearchRequest;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;
import org.springframework.stereotype.Component;

@Component
public class BoardSpecification {

    public Specification<Board> search(BoardSearchRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getTitle() != null && !request.getTitle().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + request.getTitle().toLowerCase() + "%"));
            }

            if (request.getInterestTag() != null) {
                predicates.add(cb.equal(root.get("interestTag"), request.getInterestTag()));
            }

            if (request.getMeetType() != null) {
                predicates.add(cb.equal(root.get("meetType"), request.getMeetType()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
