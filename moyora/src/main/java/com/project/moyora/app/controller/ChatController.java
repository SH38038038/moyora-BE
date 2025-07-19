package com.project.moyora.app.controller;

import com.project.moyora.app.domain.ChatParticipant;
import com.project.moyora.app.dto.ChatMessageDto;
import com.project.moyora.app.domain.ChatMessage;
import com.project.moyora.app.domain.ChatRoom;
import com.project.moyora.app.domain.User;
import com.project.moyora.app.dto.ChatRoomDto;
import com.project.moyora.app.repository.ChatMessageRepository;
import com.project.moyora.app.repository.ChatParticipantRepository;
import com.project.moyora.app.repository.ChatRoomRepository;
import com.project.moyora.app.repository.UserRepository;
import com.project.moyora.global.exception.ErrorCode;
import com.project.moyora.global.exception.model.CustomException;
import com.project.moyora.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    @GetMapping("/chatroom/{roomId}")
    public String chatRoomPage(@PathVariable Long roomId, Model model) {
        model.addAttribute("roomId", roomId);
        return "chatroom"; // chatroom.html 뷰
    }

    @GetMapping("/chatroom/{roomId}/messages")
    public ResponseEntity<List<ChatMessageDto>> getChatMessages(@PathVariable Long roomId) {
        List<ChatMessage> messages = chatMessageRepository.findByChatRoomIdOrderBySentAtAsc(roomId);
        List<ChatMessageDto> dtos = messages.stream()
                .map(msg -> {
                    ChatMessageDto dto = new ChatMessageDto();
                    dto.setRoomId(msg.getChatRoom().getId());
                    dto.setSender(msg.getSender());
                    dto.setContent(msg.getContent());
                    //dto.setSentAt(msg.getSentAt());
                    return dto;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/chatrooms/my")
    public ResponseEntity<List<ChatRoomDto>> getMyChatRooms(@AuthenticationPrincipal CustomUserDetails customUserDetails) {

        User user = customUserDetails.getUser();

        if (user == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_EXCEPTION, "인증되지 않은 사용자입니다.");
        }

        List<ChatParticipant> participants = chatParticipantRepository.findAllByUserId(user.getId());

        List<ChatRoomDto> chatRooms = participants.stream()
                .map(ChatParticipant::getChatRoom)
                .map(room -> new ChatRoomDto(room.getId(), room.getName()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(chatRooms);
    }

}
