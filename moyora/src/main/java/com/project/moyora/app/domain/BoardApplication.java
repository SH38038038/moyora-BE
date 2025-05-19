package com.project.moyora.app.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User applicant;

    @Enumerated(EnumType.STRING) // 또는 EnumType.ORDINAL
    @Column(name = "status")
    private ApplicationStatus status;

    private LocalDateTime appliedAt;
    private LocalDateTime createdAt;
}

