package com.project.moyora.app.Dto;

import com.project.moyora.app.domain.ReportStatus;
import com.project.moyora.app.domain.ReportType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReportResponse {
    private Long id;
    private String reason;
    private ReportType reportType;
    private ReportStatus status;
    private String reporterEmail;
    private String reportedTarget;
    private LocalDateTime createdAt;
    // Getters, Setters
}
