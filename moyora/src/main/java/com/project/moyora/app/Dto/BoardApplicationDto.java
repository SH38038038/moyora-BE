package com.project.moyora.app.Dto;

import com.project.moyora.app.domain.ApplicationStatus;
import com.project.moyora.app.domain.BoardApplication;

import com.project.moyora.app.domain.User;
import com.project.moyora.app.repository.LikeRepository;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardApplicationDto {
    private Long applicationId;
    private BoardListDto board;
    private ApplicationStatus applicationStatus;
/*
    // 기존 기본 from()은 유지 (필요 시 사용)
    public static BoardApplicationDto from(BoardApplication application) {
        return BoardApplicationDto.builder()
                .applicationId(application.getId())
                .board(BoardListDto.from(application.getBoard()))
                .applicationStatus(application.getStatus())
                .build();
    }
*/
    // 사용자 찜 여부까지 반영하는 from() 메서드
    public static BoardApplicationDto from(BoardApplication application, User user, LikeRepository likeRepository) {
        return BoardApplicationDto.builder()
                .applicationId(application.getId())
                .board(BoardListDto.from(application.getBoard(), user, likeRepository)) // liked 반영
                .applicationStatus(application.getStatus())
                .build();
    }
}

