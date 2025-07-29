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
public class ChatNoticeComment {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private ChatMessage notice; // 공지 채팅

    @ManyToOne
    private User writer;

    private String content;

    private LocalDateTime createdAt;
}
