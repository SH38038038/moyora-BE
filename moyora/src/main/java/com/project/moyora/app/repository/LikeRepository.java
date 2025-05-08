package com.project.moyora.app.repository;

import com.project.moyora.app.domain.Board;
import com.project.moyora.app.domain.Like;
import com.project.moyora.app.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserAndBoard(User user, Board board);
    @Query("SELECT l FROM Like l JOIN FETCH l.board WHERE l.user = :user")
    List<Like> findByUserWithBoard(@Param("user") User user);
    boolean existsByUserAndBoard(User user, Board board);


}

