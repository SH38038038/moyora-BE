package com.project.moyora.app.controller;

import com.project.moyora.app.domain.ChatMessage;
import com.project.moyora.app.domain.ChatNoticeComment;
import com.project.moyora.app.domain.ChatRoom;
import com.project.moyora.app.dto.ChatNoticeCommentDto;
import com.project.moyora.app.dto.ChatNoticeDto;
import com.project.moyora.app.repository.*;
import com.project.moyora.app.service.ChatNoticeService;
import com.project.moyora.global.exception.ErrorCode;
import com.project.moyora.global.exception.model.CustomException;
import com.project.moyora.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatNoticeController {

    private final ChatNoticeService chatNoticeService;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatNoticeCommentRepository chatNoticeCommentRepository;
    private final ChatNoticeLikeRepository chatNoticeLikeRepository;

    private final UserRepository userRepository;

    // 1. 공지 설정
    @PostMapping("/{chatRoomId}/notice/{messageId}")
    public ResponseEntity<Void> setChatNotice(
            @PathVariable Long chatRoomId,
            @PathVariable Long messageId) {
        chatNoticeService.setChatNotice(chatRoomId, messageId);
        return ResponseEntity.ok().build();
    }

    // 2. 공지 댓글 작성
    @PostMapping("/notice/{chatNoticeId}/comment")
    public ResponseEntity<Void> addComment(
            @PathVariable Long chatNoticeId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody Map<String, String> body) {

        String content = body.get("content");
        Long userId = userDetails.getUser().getId();

        chatNoticeService.addComment(chatNoticeId, userId, content);
        return ResponseEntity.ok().build();
    }

    // 3. 공지 좋아요 토글
    @PostMapping("/notice/{chatNoticeId}/like")
    public ResponseEntity<Void> toggleLike(
            @PathVariable Long chatNoticeId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userId = userDetails.getUser().getId();
        chatNoticeService.toggleLike(chatNoticeId, userId);
        return ResponseEntity.ok().build();
    }

    // 4. 공지 + 댓글 + 좋아요 개수 조회 (선택)
    @GetMapping("/{chatRoomId}/notice")
    public ResponseEntity<ChatNoticeDto> getNoticeInfo(
            @PathVariable Long chatRoomId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHATROOM_NOT_FOUND,null));

        ChatMessage notice = chatMessageRepository.findByChatRoomAndIsNoticeTrue(chatRoom)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTICE_NOT_FOUND, "공지사항이 없습니다."));

        List<ChatNoticeComment> comments = chatNoticeCommentRepository.findByNoticeIdOrderByCreatedAtAsc(notice.getId());

        long likeCount = chatNoticeLikeRepository.countByNotice(notice);
        Long currentUserId = userDetails.getUser().getId();
        boolean likedByCurrentUser = chatNoticeLikeRepository.findByNoticeAndUser(notice, userRepository.findById(currentUserId).get()).isPresent();

        ChatNoticeDto dto = new ChatNoticeDto();
        dto.setId(notice.getId());
        dto.setChatRoomId(chatRoomId);
        dto.setContent(notice.getContent());
        dto.setSenderName(notice.getSender());
        dto.setNotice(true);
        dto.setLikeCount(likeCount);
        dto.setLikedByCurrentUser(likedByCurrentUser);

        List<ChatNoticeCommentDto> commentDtos = comments.stream().map(c -> {
            ChatNoticeCommentDto commentDto = new ChatNoticeCommentDto();
            commentDto.setId(c.getId());
            commentDto.setChatNoticeId(c.getNotice().getId());
            commentDto.setWriterName(c.getWriter().getName());
            commentDto.setContent(c.getContent());
            commentDto.setCreatedAt(c.getCreatedAt());
            return commentDto;
        }).collect(Collectors.toList());

        dto.setComments(commentDtos);

        return ResponseEntity.ok(dto);
    }

    private final ChatRoomRepository chatRoomRepository;
}
