package com.project.moyora.app.repository;

import com.project.moyora.app.domain.ChatNoticeComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatNoticeCommentRepository extends JpaRepository<ChatNoticeComment, Long> {
    List<ChatNoticeComment> findByNoticeIdOrderByCreatedAtAsc(Long chatNoticeId);
}