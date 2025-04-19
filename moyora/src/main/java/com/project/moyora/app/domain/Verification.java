package com.project.moyora.app.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Verification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private VerificationStatus status; // PENDING, ACCEPTED, REJECTED

    private String reason; // 거절 이유

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
