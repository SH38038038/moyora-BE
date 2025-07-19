package com.project.moyora.app.dto;

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

    private Long reporterId;
    private String reporterName;

    private Long reportedId;
    private String reportedName;

    private String reportedContent;

    private LocalDateTime createdAt;
}
