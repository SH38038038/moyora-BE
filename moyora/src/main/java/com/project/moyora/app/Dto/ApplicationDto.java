package com.project.moyora.app.dto;

import com.project.moyora.app.domain.ApplicationStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationDto {
    private Long boardId;
    private String applicantEmail;
    private ApplicationStatus status;
}

