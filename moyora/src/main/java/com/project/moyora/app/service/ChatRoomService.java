package com.project.moyora.app.service;

import com.project.moyora.app.domain.*;
import com.project.moyora.app.repository.BoardApplicationRepository;
import com.project.moyora.app.repository.ChatRoomRepository;
import com.project.moyora.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final BoardApplicationRepository applicationRepository;
    private final UserRepository userRepository;

    @Transactional
    public ChatRoom createRoomForLockedBoard(Board board) {
        if (chatRoomRepository.existsByBoard(board)) return null; // 중복 방지

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
}
