package com.project.moyora.app.service;

import com.project.moyora.app.domain.ChatMessage;
import com.project.moyora.app.domain.ChatNoticeComment;
import com.project.moyora.app.domain.ChatRoom;
import com.project.moyora.app.domain.ChatNoticeLike;
import com.project.moyora.app.domain.User;
import com.project.moyora.app.repository.*;
import com.project.moyora.global.exception.ErrorCode;
import com.project.moyora.global.exception.model.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatNoticeService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatNoticeCommentRepository chatNoticeCommentRepository;
    private final ChatNoticeLikeRepository chatNoticeLikeRepository;
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;

    @Transactional
    public void setChatNotice(Long chatRoomId, Long messageId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHATROOM_NOT_FOUND,null));

        chatMessageRepository.findByChatRoomAndIsNoticeTrue(chatRoom)
                .ifPresent(existingNotice -> {
                    existingNotice.setNotice(false);
                    chatMessageRepository.save(existingNotice);
                });

        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHATMESSAGE_NOT_FOUND,null));

        if (!message.getChatRoom().getId().equals(chatRoomId)) {
            throw new CustomException(ErrorCode.INVALID_REQUEST, "공지 메시지가 해당 채팅방에 속하지 않습니다.");
        }

        message.setNotice(true);
        chatMessageRepository.save(message);
    }

    @Transactional
    public void addComment(Long chatNoticeId, Long userId, String content) {
        ChatMessage chatNotice = chatMessageRepository.findById(chatNoticeId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHATMESSAGE_NOT_FOUND,null));

        if (!Boolean.TRUE.equals(chatNotice.isNotice())) {
            throw new CustomException(ErrorCode.INVALID_REQUEST, "해당 메시지는 공지사항이 아닙니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND,null));

        ChatNoticeComment comment = new ChatNoticeComment();
        comment.setNotice(chatNotice);
        comment.setWriter(user);
        comment.setContent(content);
        comment.setCreatedAt(LocalDateTime.now());

        chatNoticeCommentRepository.save(comment);
    }

    @Transactional
    public void toggleLike(Long chatNoticeId, Long userId) {
        ChatMessage chatNotice = chatMessageRepository.findById(chatNoticeId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHATMESSAGE_NOT_FOUND,null));

        if (!Boolean.TRUE.equals(chatNotice.isNotice())) {
            throw new CustomException(ErrorCode.INVALID_REQUEST, "해당 메시지는 공지사항이 아닙니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND,null));

        Optional<ChatNoticeLike> existingLike = chatNoticeLikeRepository.findByNoticeAndUser(chatNotice, user);

        if (existingLike.isPresent()) {
            chatNoticeLikeRepository.delete(existingLike.get());
        } else {
            ChatNoticeLike like = new ChatNoticeLike();
            like.setNotice(chatNotice);
            like.setUser(user);
            chatNoticeLikeRepository.save(like);
        }
    }
}
