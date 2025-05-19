package com.project.moyora.app.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board; // Board와 연결

    @ManyToOne(fetch = FetchType.LAZY)
    private User writer;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createdTime;
    private LocalDateTime updateTime;

    @Builder.Default
    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NoticeComment> comments = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updateTime = LocalDateTime.now();
    }
}
