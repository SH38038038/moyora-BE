package com.project.moyora.app.repository;

import com.project.moyora.app.domain.ChatMessage;
import com.project.moyora.app.domain.ChatRoom;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // 특정 채팅방의 메시지를 시간순으로 조회
    List<ChatMessage> findByChatRoomIdOrderBySentAtAsc(Long chatRoomId);
    Optional<ChatMessage> findByChatRoomAndIsNoticeTrue(ChatRoom chatRoom);


    @Query("""
    SELECT m FROM ChatMessage m
    WHERE m.chatRoom.id = :roomId
    AND (:lastMessageId IS NULL OR m.id < :lastMessageId)
    ORDER BY m.id DESC
""")
    List<ChatMessage> findPreviousMessages(
            @Param("roomId") Long roomId,
            @Param("lastMessageId") Long lastMessageId,
            Pageable pageable
    );
}
