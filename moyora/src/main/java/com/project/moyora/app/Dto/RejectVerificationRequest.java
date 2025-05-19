package com.project.moyora.app.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RejectVerificationRequest {
    private String reason; // 필드명은 controller와 서비스에서 일치해야 합니다
}