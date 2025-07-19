package com.project.moyora.app.dto;

import com.project.moyora.app.domain.Board;
import com.project.moyora.app.domain.MeetType;
import com.project.moyora.global.tag.InterestTag;

import java.util.List;

public record BoardResponseDto(
        Long id,
        String title,
        String content,
        MeetType meetType,
        List<InterestTag> interestTags,  // 단일 -> 리스트로 변경
        UserSimpleDto writer
) {
    public static BoardResponseDto from(Board board) {
        return new BoardResponseDto(
                board.getId(),
                board.getTitle(),
                board.getContent(),
                board.getMeetType(),
                board.getTags(),  // List<InterestTag> 그대로 사용
                UserSimpleDto.from(board.getWriter())
        );
    }
}
