package com.project.moyora.app.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NoticeRequest {
    private String title;
    private String content;
    //private Long boardId;
}
