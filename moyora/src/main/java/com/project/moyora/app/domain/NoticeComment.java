package com.project.moyora.app.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class NoticeComment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User writer;

    @ManyToOne(fetch = FetchType.LAZY)
    private Notice notice;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createdTime;

    @PrePersist
    protected void onCreate() {
        this.createdTime = LocalDateTime.now();
    }
}
