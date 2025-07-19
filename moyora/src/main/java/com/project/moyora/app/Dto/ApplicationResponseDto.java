package com.project.moyora.app.dto;

import com.project.moyora.app.domain.ApplicationStatus;
import com.project.moyora.app.domain.BoardApplication;
import com.project.moyora.app.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class ApplicationResponseDto {

    private Long applicationId;
    private String applicantName;
    private int applicantAge;
    private String applicantGender;
    private boolean applicantVerified;
    private List<TagDto> applicantInterestTags;
    private ApplicationStatus status;

    public static ApplicationResponseDto from(BoardApplication application) {
        User applicant = application.getApplicant();

        return new ApplicationResponseDto(
                application.getId(),
                applicant.getName(),
                applicant.getAge(),
                applicant.getGender() != null ? applicant.getGender().name() : null,
                applicant.getVerified(),
                applicant.getInterestTags().stream()
                        .map(TagDto::from)
                        .collect(Collectors.toList()),
                application.getStatus()
        );
    }
}
