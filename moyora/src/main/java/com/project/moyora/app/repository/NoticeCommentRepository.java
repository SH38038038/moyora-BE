package com.project.moyora.app.repository;

import com.project.moyora.app.domain.NoticeComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeCommentRepository extends JpaRepository<NoticeComment, Long> {
}
