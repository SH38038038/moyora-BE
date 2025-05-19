package com.project.moyora.app.Dto;

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
    private String writerNickname;
    private String content;
    private LocalDateTime createdTime;

    public static CommentDto fromEntity(NoticeComment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .writerNickname(comment.getWriter().getName())
                .content(comment.getContent())
                .createdTime(comment.getCreatedTime())
                .build();
    }
}
