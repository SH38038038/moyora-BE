package com.project.moyora.app.repository;

import com.project.moyora.app.domain.Board;
import com.project.moyora.app.domain.BoardApplication;
import com.project.moyora.app.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<BoardApplication, Long> {

    boolean existsByBoardAndApplicant(Board board, User applicant);

    List<BoardApplication> findByBoard(Board board);

    @Query("SELECT a FROM BoardApplication a JOIN FETCH a.applicant JOIN FETCH a.board b WHERE b.id = :boardId")
    List<BoardApplication> findByBoardIdWithApplicants(@Param("boardId") Long boardId);

    List<BoardApplication> findByApplicant(User applicant);

    Optional<BoardApplication> findByBoardAndApplicant(Board board, User applicant);

    @Query("SELECT a.applicant FROM BoardApplication a " +
            "WHERE a.board.id = :boardId AND a.status = 'ACCEPTED'")
    List<User> findAcceptedApplicantsByBoardId(@Param("boardId") Long boardId);

}
