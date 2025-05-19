package com.project.moyora.app.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportPostRequest {
    private Long boardId;
    private String reason;
}
