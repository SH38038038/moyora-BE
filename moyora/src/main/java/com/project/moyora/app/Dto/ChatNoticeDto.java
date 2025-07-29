package com.project.moyora.app.dto;

import lombok.Data;

import java.util.List;

@Data
public class ChatNoticeDto {
    private Long id;
    private Long chatRoomId;
    private String content;
    private String senderName;
    private boolean isNotice;

    private List<ChatNoticeCommentDto> comments;
    private long likeCount;
    private boolean likedByCurrentUser;
}