package com.project.moyora.app.Dto;

import com.project.moyora.app.domain.BoardApplication;
import com.project.moyora.app.domain.GenderType;
import com.project.moyora.app.domain.User;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Participant {

    private Long userId; // 사용자 ID
    private String applicantName;
    private int applicantAge;
    private GenderType applicantGender;
    private boolean applicantVerified;

    public static Participant fromEntity(BoardApplication application) {
        User applicant = application.getApplicant();

        return Participant.builder()
                .userId(applicant.getId()) // userId 설정
                .applicantName(applicant.getName())
                .applicantAge(applicant.getAge())
                .applicantGender(applicant.getGender())
                .applicantVerified(applicant.getVerified())

                .build();
    }
}
