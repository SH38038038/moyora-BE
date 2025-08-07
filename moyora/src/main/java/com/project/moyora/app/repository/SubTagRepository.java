package com.project.moyora.app.repository;

import com.project.moyora.app.domain.SubTag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface SubTagRepository extends Repository<SubTag, Long> {

    @Query("SELECT s.name, COUNT(s.board.id) FROM SubTag s GROUP BY s.name ORDER BY COUNT(s.board.id) DESC")
    List<Object[]> findTopSubTags(Pageable pageable);
}
