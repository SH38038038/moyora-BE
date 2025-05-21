package com.project.moyora.app.repository;

import com.project.moyora.app.domain.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    List<Notice> findByBoardId(Long boardId);

    @Query("SELECT n FROM Notice n JOIN FETCH n.writer WHERE n.id = :id")
    Optional<Notice> findByIdWithWriter(@Param("id") Long id);

    @Query("SELECT n FROM Notice n " +
            "LEFT JOIN FETCH n.writer " +
            "LEFT JOIN FETCH n.comments " +
            "WHERE n.id = :id")
    Optional<Notice> findByIdWithWriterAndComments(@Param("id") Long id);

    @Query("SELECT DISTINCT n FROM Notice n " +
            "LEFT JOIN FETCH n.writer " +
            "LEFT JOIN FETCH n.comments")
    List<Notice> findAllWithWriterAndComments();

    @Query("SELECT DISTINCT n FROM Notice n " +
            "LEFT JOIN FETCH n.writer " +
            "LEFT JOIN FETCH n.comments " +
            "WHERE n.board.id = :boardId")
    List<Notice> findAllByBoardIdWithWriterAndComments(@Param("boardId") Long boardId);

}
