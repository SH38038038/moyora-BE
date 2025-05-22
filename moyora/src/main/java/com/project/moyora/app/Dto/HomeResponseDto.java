package com.project.moyora.app.Dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HomeResponseDto {
    private List<BoardListDto> recommendedBoards;
    private List<TagDto> popularTags;
}
