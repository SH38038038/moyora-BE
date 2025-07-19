package com.project.moyora.app.dto;

import lombok.Data;

@Data
public class ChatMessageDto {
    private Long roomId;
    private String sender;
    private String content;
}
