package com.project.moyora.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatRoomDto {
    private Long id;
    private String name;  // 채팅방 이름, 없으면 null 가능
}
