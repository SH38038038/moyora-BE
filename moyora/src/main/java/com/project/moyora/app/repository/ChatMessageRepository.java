package com.project.moyora.app.repository;

import com.project.moyora.app.domain.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // 특정 채팅방의 메시지를 시간순으로 조회
    List<ChatMessage> findByChatRoomIdOrderBySentAtAsc(Long chatRoomId);
}
