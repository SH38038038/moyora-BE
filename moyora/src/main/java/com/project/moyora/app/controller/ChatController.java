package com.project.moyora.app.controller;

import com.project.moyora.app.Dto.ChatMessageDto;
import com.project.moyora.app.domain.ChatMessage;
import com.project.moyora.app.domain.ChatRoom;
import com.project.moyora.app.domain.User;
import com.project.moyora.app.repository.ChatMessageRepository;
import com.project.moyora.app.repository.ChatParticipantRepository;
import com.project.moyora.app.repository.ChatRoomRepository;
import com.project.moyora.app.repository.UserRepository;
import com.project.moyora.global.exception.ErrorCode;
import com.project.moyora.global.exception.model.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final ChatParticipantRepository chatParticipantRepository;

    @MessageMapping("/chat.send")
    public void sendMessage(ChatMessageDto messageDto, Principal principal) {
        if (principal == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_EXCEPTION, "인증되지 않은 사용자입니다.");
        }

        String email = principal.getName();
        User sender = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER_EXCEPTION, "유저를 찾을 수 없음"));

        ChatRoom room = chatRoomRepository.findById(messageDto.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("채팅방 없음"));

        boolean isParticipant = chatParticipantRepository
                .existsByChatRoomIdAndUserId(room.getId(), sender.getId());

        if (!isParticipant) {
            throw new CustomException(ErrorCode.ACCESS_DENIED_EXCEPTION, "채팅방 참여자가 아닙니다.");
        }

        ChatMessage msg = new ChatMessage();
        msg.setChatRoom(room);
        msg.setContent(messageDto.getContent());
        msg.setSender(sender.getName());
        msg.setSentAt(LocalDateTime.now());
        chatMessageRepository.save(msg);

        log.info("🔥 메시지 수신됨: roomId={}, sender={}, content={}",
                messageDto.getRoomId(), sender.getName(), messageDto.getContent());

        ChatMessageDto sendDto = new ChatMessageDto();
        sendDto.setRoomId(room.getId());
        sendDto.setSender(sender.getName());
        sendDto.setContent(messageDto.getContent());

        messagingTemplate.convertAndSend("/topic/chatroom/" + room.getId(), sendDto);
    }
}
