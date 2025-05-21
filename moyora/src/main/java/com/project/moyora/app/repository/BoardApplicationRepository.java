package com.project.moyora.app.repository;

import com.project.moyora.app.domain.ApplicationStatus;
import com.project.moyora.app.domain.Board;
import com.project.moyora.app.domain.BoardApplication;
import com.project.moyora.app.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardApplicationRepository extends JpaRepository<BoardApplication, Long> {

    @Query("SELECT ba.board FROM BoardApplication ba WHERE ba.applicant = :user AND ba.status = 'LOCKED'")
    List<Board> findAcceptedBoardsByUser(@Param("user") User user);
    boolean existsByBoardAndApplicantAndStatus(Board board, User applicant, ApplicationStatus status);
    Optional<BoardApplication> findByBoard_IdAndApplicant_Name(Long boardId, String name);
    List<BoardApplication> findByBoard(Board board);
    @Query("""
    SELECT ba FROM BoardApplication ba
    JOIN FETCH ba.applicant a
    LEFT JOIN FETCH a.interestTags
    WHERE ba.board = :board
""")
    List<BoardApplication> findWithApplicantAndTagsByBoard(@Param("board") Board board);

}
