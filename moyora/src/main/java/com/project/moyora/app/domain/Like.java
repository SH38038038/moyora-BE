package com.project.moyora.app.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@Entity
@Table(name = "likes")
@NoArgsConstructor
@AllArgsConstructor
public class Like {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user; // 찜한 사용자

    @ManyToOne(fetch = FetchType.LAZY)
    private Board board; // 찜한 게시물

    @PrePersist
    protected void onCreate() {
        // 생성 시점에서 추가 작업이 필요하다면 여기에 작성
    }
}
