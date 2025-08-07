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
        List<InterestTag> interestTags,
        List<String> sub_tags,
        UserSimpleDto writer
) {
    public static BoardResponseDto from(Board board) {
        List<String> subTagNames = board.getSubTags().stream()
                .map(subTag -> subTag.getName())
                .toList();

        return new BoardResponseDto(
                board.getId(),
                board.getTitle(),
                board.getContent(),
                board.getMeetType(),
                board.getTags(),
                subTagNames,
                UserSimpleDto.from(board.getWriter())
        );
    }
}
