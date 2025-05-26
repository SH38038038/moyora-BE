package com.project.moyora.app.repository;

import com.project.moyora.app.domain.Board;
import com.project.moyora.app.domain.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    // 게시판에 공지 존재 여부 확인
    boolean existsByBoard(Board board);

    // 게시판 ID로 공지사항 1건 가져오기 (댓글 + 작성자 포함)
    @Query("""
    SELECT DISTINCT n FROM Notice n
    JOIN FETCH n.writer
    JOIN FETCH n.board b
    LEFT JOIN FETCH b.applications
    LEFT JOIN FETCH n.comments
    WHERE b.id = :boardId
""")
    Optional<Notice> findByBoardIdWithWriterAndComments(@Param("boardId") Long boardId);

    // 공지 ID로 조회 (댓글 + 작성자 포함)
    @Query("SELECT n FROM Notice n " +
            "JOIN FETCH n.writer " +
            "LEFT JOIN FETCH n.comments " +
            "WHERE n.id = :id")
    Optional<Notice> findByIdWithWriterAndComments(@Param("id") Long id);
}
