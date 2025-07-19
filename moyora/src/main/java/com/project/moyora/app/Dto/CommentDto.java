package com.project.moyora.app.dto;

import com.project.moyora.app.domain.NoticeComment;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;
    private String writer;
    private String content;
    private LocalDateTime createdTime;

    public static CommentDto fromEntity(NoticeComment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .writer(comment.getWriter().getName())
                .content(comment.getContent())
                .createdTime(comment.getCreatedTime())
                .build();
    }
}
