package com.project.moyora.app.service;

import com.project.moyora.app.Dto.BoardSearchRequest;
import com.project.moyora.app.domain.Board;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;

public class BoardSpecification {

    public static Specification<Board> search(BoardSearchRequest request) {
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
