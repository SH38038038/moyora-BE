package com.project.moyora.app.repository;

import com.project.moyora.app.domain.ChatMessage;
import com.project.moyora.app.domain.ChatNoticeLike;
import com.project.moyora.app.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatNoticeLikeRepository extends JpaRepository<ChatNoticeLike, Long> {
    Optional<ChatNoticeLike> findByNoticeAndUser(ChatMessage chatNotice, User user);
    long countByNotice(ChatMessage chatNotice);
}
