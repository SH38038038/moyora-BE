package com.project.moyora.app.repository;

import com.project.moyora.app.domain.Board;
import com.project.moyora.app.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long>, JpaSpecificationExecutor<Board> {
    List<Board> findAllByOrderByCreatedTimeDesc(); // 최신순 조회

    @Query("SELECT b FROM Board b WHERE b.id = :id")
    Board findBoardById(@Param("id") Long id);

    List<Board> findByWriter(User user);
}

