package com.project.moyora.app.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportUserRequest {
    private Long reportedUserId;
    private String reason;
}
