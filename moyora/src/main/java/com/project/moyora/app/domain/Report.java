package com.project.moyora.app.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Report {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String reason;

    @Enumerated(EnumType.STRING)
    private ReportType reportType;

    @Enumerated(EnumType.STRING)
    private ReportStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    private User reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    private User reportedUser;

    @ManyToOne(fetch = FetchType.LAZY)
    private Board reportedBoard;

    private LocalDateTime createdAt;

    // Getters, Setters
}

