package com.project.moyora.app.Dto;

import lombok.Data;

@Data
public class ChatMessageDto {
    private Long roomId;
    private String sender;
    private String content;
}
