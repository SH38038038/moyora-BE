package com.project.moyora.app.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class ChatMessage {
    @Id
    @GeneratedValue
    private Long id;

    private String sender;
    private String content;

    private LocalDateTime sentAt;

    @ManyToOne
    private ChatRoom chatRoom;
}
