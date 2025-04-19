package com.project.moyora.app.Dto;

import com.project.moyora.app.domain.GenderType;
import com.project.moyora.app.domain.VerificationStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class VerificationResponse {
    private Long id;
    private String email;
    private String idcardUrl;
    private LocalDate birth;
    private GenderType gender;
    private VerificationStatus status;
    private String detailUrl; // 세부 조회 URL

    // 생성자, Getter/Setter 추가

    public VerificationResponse(Long id, String email, String idcardUrl, LocalDate birth, GenderType gender, VerificationStatus status, String detailUrl) {
        this.id = id;
        this.email = email;
        this.idcardUrl = idcardUrl;
        this.birth = birth;
        this.gender = gender;
        this.status = status;
        this.detailUrl = detailUrl;
    }
}

