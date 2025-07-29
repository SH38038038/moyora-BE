package com.project.moyora.app.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatNoticeCommentDto {
    private Long id;
    private Long chatNoticeId;
    private String writerName;
    private String content;
    private LocalDateTime createdAt;
}
