package com.project.moyora.global.config;
/*
import com.project.moyora.app.domain.*;
import com.project.moyora.app.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Override
    public void run(String... args) throws Exception {
        // 1. 유저 생성
        User user1 = User.builder()
                .email("user1@example.com")
                .name("User One")
                .roleType(RoleType.USER)
                .build();

        User user2 = User.builder()
                .email("user2@example.com")
                .name("User Two")
                .roleType(RoleType.USER)
                .build();

        userRepository.saveAll(List.of(user1, user2));

        // 2. 채팅방 생성
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setName("기본 채팅방");
        chatRoomRepository.save(chatRoom);

        // 3. 참여자 추가
        ChatParticipant participant1 = new ChatParticipant();
        participant1.setChatRoom(chatRoom);
        participant1.setUser(user1);

        ChatParticipant participant2 = new ChatParticipant();
        participant2.setChatRoom(chatRoom);
        participant2.setUser(user2);

        chatParticipantRepository.saveAll(List.of(participant1, participant2));

        // 4. 초기 메시지 추가
        ChatMessage message = new ChatMessage();
        message.setChatRoom(chatRoom);
        message.setSender(user1.getName());
        message.setContent("안녕하세요! 초기 메시지입니다.");
        message.setSentAt(LocalDateTime.now());
        chatMessageRepository.save(message);

        System.out.println("✅ 초기 데이터 세팅 완료");
    }
}
*/