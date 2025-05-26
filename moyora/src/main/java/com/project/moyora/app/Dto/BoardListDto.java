package com.project.moyora.app.Dto;

import com.project.moyora.app.domain.Board;
import com.project.moyora.app.domain.MeetType;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.time.LocalDate;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BoardListDto {
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private MeetType meetType;
    private String meetDetail;
    private List<TagDto> tags;
    private Integer howMany;
    private Integer participation;
    private String detailUrl;

    private boolean liked;
    private boolean confirmed;  // 확정 여부 (참여 인원 다 찼을 때만 변경 가능)


    public static BoardListDto from(Board board) {
        List<TagDto> tagDtos = Collections.emptyList();

        if (board.getTags() != null && !board.getTags().isEmpty()) {
            tagDtos = board.getTags().stream()
                    .filter(Objects::nonNull)
                    .map(tag -> {
                        TagDto dto = TagDto.from(tag);
                        log.debug("[DEBUG] TagDto.from() 호출 후 dto.name = {}", dto.getName());
                        return dto;
                    })
                    .collect(Collectors.toList());
        }

        log.debug("[DEBUG] BoardListDto.from() tags size = {}", tagDtos.size());
        tagDtos.forEach(t -> log.debug("[DEBUG] TagDto in list: name={}, displayName={}", t.getName(), t.getDisplayName()));

        return BoardListDto.builder()
                .title(board.getTitle())
                .startDate(board.getStartDate())
                .endDate(board.getEndDate())
                .meetType(board.getMeetType())
                .meetDetail(board.getMeetDetail())
                .tags(tagDtos)
                .howMany(board.getHowMany())
                .participation(board.getParticipation())
                .detailUrl("/boards/" + board.getId())
                .liked(false)
                .confirmed(board.isConfirmed())
                .build();

    }
}