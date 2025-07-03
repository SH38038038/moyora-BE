package com.project.moyora.app.repository;

import com.project.moyora.app.domain.Board;
import com.project.moyora.app.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    // Board를 기준으로 채팅방 존재 여부 확인 (중복 방지용)
    boolean existsByBoard(Board board);

    // Board ID로 채팅방 조회 (필요시)
    Optional<ChatRoom> findByBoard(Board board);
}
