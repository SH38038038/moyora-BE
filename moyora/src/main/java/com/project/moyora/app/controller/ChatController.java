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
import com.project.moyora.app.service.ChatService;
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
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Comparator;
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
    private final ChatService chatService;

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

    // 기존
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

    // 무한 스크롤
    @GetMapping("/chatroom/{roomId}/messages/page")
    public ResponseEntity<List<ChatMessageDto>> getPreviousMessages(
            @PathVariable Long roomId,
            @RequestParam(required = false) Long lastMessageId,  // 마지막으로 받은 메시지 id
            @RequestParam(defaultValue = "20") int size            // 한번에 불러올 메시지 수
    ) {
        List<ChatMessage> messages = chatService.getPreviousMessages(roomId, lastMessageId, size);

        // 클라이언트가 편하게 보게 오름차순(시간순)으로 정렬 변경
        List<ChatMessageDto> dtos = messages.stream()
                .sorted(Comparator.comparing(ChatMessage::getId))
                .map(msg -> {
                    ChatMessageDto dto = new ChatMessageDto();
                    dto.setRoomId(msg.getChatRoom().getId());
                    dto.setSender(msg.getSender());
                    dto.setContent(msg.getContent());
                    // dto.setSentAt(msg.getSentAt());
                    dto.setId(msg.getId());  // 커서용 id 포함 필드 추가 필요
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
