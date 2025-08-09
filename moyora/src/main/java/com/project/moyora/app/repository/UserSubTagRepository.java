package com.project.moyora.app.repository;

import com.project.moyora.app.domain.Category;
import com.project.moyora.app.domain.SubTag;
import com.project.moyora.app.domain.User;
import com.project.moyora.app.domain.UserSubTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface UserSubTagRepository extends JpaRepository<UserSubTag, Long> {
    List<UserSubTag> findByUserAndCategory(User user, Category category);

    // user 전체 카테고리별 태그 조회
    @Query("SELECT DISTINCT ust.subTag FROM UserSubTag ust WHERE ust.user = :user")
    List<SubTag> findDistinctSubTagsByUser(@Param("user") User user);

    // 특정 user와 subTag 존재 여부 확인
    boolean existsByUserAndSubTag(User user, SubTag subTag);

    // user와 category 기준 삭제 (참여 취소 시)
    @Modifying
    @Transactional
    void deleteByUserAndCategory(User user, Category category);

    void deleteByUserAndSubTagAndCategory(User user, SubTag subTag, Category category);


    Optional<UserSubTag> findByUserAndSubTagId(User user, Long subTagId);

}