package com.project.moyora.app.service;

import com.project.moyora.app.domain.*;
import com.project.moyora.app.repository.BoardApplicationRepository;
import com.project.moyora.app.repository.ChatMessageRepository;
import com.project.moyora.app.repository.ChatRoomRepository;
import com.project.moyora.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final BoardApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Transactional
    public ChatRoom createRoomForLockedBoard(Board board) {
        Optional<ChatRoom> existing = chatRoomRepository.findByBoard(board);
        if (existing.isPresent()) return existing.get(); // 이미 있으면 그거 사용

        ChatRoom room = new ChatRoom();
        room.setBoard(board);
        room.setName(board.getTitle() + " 채팅방");

        List<User> participants = applicationRepository.findUsersByBoardAndStatus(board.getId(), ApplicationStatus.LOCKED);
        participants.add(board.getWriter());

        for (User user : participants) {
            ChatParticipant participant = new ChatParticipant();
            participant.setChatRoom(room);
            participant.setUser(user);
            room.getParticipants().add(participant);
        }

        return chatRoomRepository.save(room);
    }

    @Transactional
    public void deleteChatRoomByBoardId(Long boardId) {
        ChatRoom chatRoom = chatRoomRepository.findByBoardId(boardId)
                .orElse(null);
        if (chatRoom != null) {
            // 1. chat_message 먼저 삭제
            chatMessageRepository.deleteByChatRoomId(chatRoom.getId());

            // 2. chat_room 삭제
            chatRoomRepository.delete(chatRoom);
        }
    }


}
