package com.project.moyora.app.repository;

import com.project.moyora.app.domain.Board;
import com.project.moyora.app.domain.MeetType;
import com.project.moyora.app.domain.SubTag;
import com.project.moyora.app.domain.User;
import com.project.moyora.global.tag.InterestTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface BoardRepository extends JpaRepository<Board, Long>, JpaSpecificationExecutor<Board> {
    List<Board> findAllByOrderByCreatedTimeDesc(); // 최신순 조회

    @Query("SELECT b FROM Board b WHERE b.id = :id")
    Board findBoardById(@Param("id") Long id);

    List<Board> findByWriter(User user);

    @Query("""
    SELECT DISTINCT b FROM Board b
    JOIN b.tags t
    WHERE (:keyword IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')))
    AND (:interestTags IS NULL OR t IN :interestTags)
    AND (:meetType IS NULL OR b.meetType = :meetType)
    AND (:meetDetail IS NULL OR b.meetDetail = :meetDetail)
    """)
    List<Board> searchBoardsWithUserTags(
            @Param("keyword") String keyword,
            @Param("interestTags") List<InterestTag> interestTags,
            @Param("meetType") MeetType meetType,
            @Param("meetDetail") String meetDetail
    );


    Page<Board> findDistinctByTagsInOrderByCreatedTimeDesc(Set<InterestTag> tags, Pageable pageable);

    @Query(value = "SELECT tags FROM board_interest_tags GROUP BY tags ORDER BY COUNT(*) DESC LIMIT :limit", nativeQuery = true)
    List<String> findPopularTags(@Param("limit") int limit);

    @Query("""
    SELECT b FROM Board b
    WHERE b.writer.id <> :userId
    AND NOT EXISTS (
        SELECT 1 FROM BoardApplication ba
        WHERE ba.board = b
        AND ba.applicant.id = :userId
        AND ba.status IN ('WAITING', 'ACCEPTED', 'LOCKED')
    )
    ORDER BY function('RAND')
""")
    Page<Board> findRandomBoardsExcludingUser(@Param("userId") Long userId, Pageable pageable);


    @Query("""
    SELECT DISTINCT b FROM Board b
    JOIN b.subTags st
    WHERE st IN :subTags
    AND b.writer.id <> :userId
    AND NOT EXISTS (
        SELECT 1 FROM BoardApplication ba 
        WHERE ba.board = b AND ba.applicant.id = :userId AND ba.status IN ('WAITING', 'ACCEPTED', 'LOCKED')
    )
    ORDER BY b.createdTime DESC
""")
    Page<Board> findRecommendedBoardsByUserSubTagsExcludingUserApplications(
            @Param("subTags") Set<SubTag> subTags,
            @Param("userId") Long userId,
            Pageable pageable);

}

