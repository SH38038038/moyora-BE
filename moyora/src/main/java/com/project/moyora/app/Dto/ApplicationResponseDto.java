package com.project.moyora.app.Dto;

import com.project.moyora.app.domain.ApplicationStatus;
import com.project.moyora.app.domain.BoardApplication;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApplicationResponseDto {
    private Long applicationId;
    private String applicantEmail;
    private ApplicationStatus status;

    public static ApplicationResponseDto from(BoardApplication application) {
        return new ApplicationResponseDto(
                application.getId(),
                application.getApplicant().getEmail(),
                application.getStatus()
        );
    }
}
