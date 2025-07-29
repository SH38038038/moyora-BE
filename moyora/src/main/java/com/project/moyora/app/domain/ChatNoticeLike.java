package com.project.moyora.app.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ChatNoticeLike {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private ChatMessage notice;

    @ManyToOne
    private User user;
}
