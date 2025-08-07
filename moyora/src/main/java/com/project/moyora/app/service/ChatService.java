package com.project.moyora.app.service;

import com.project.moyora.app.domain.ChatMessage;
import com.project.moyora.app.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;

    public List<ChatMessage> getPreviousMessages(Long roomId, Long lastMessageId, int size) {
        Pageable pageable = PageRequest.of(0, size); // 최근 메시지부터 size개
        return chatRoomRepository.findPreviousMessages(roomId, lastMessageId, pageable);
    }
}
