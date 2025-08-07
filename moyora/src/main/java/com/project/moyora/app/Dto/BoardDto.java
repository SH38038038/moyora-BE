package com.project.moyora.app.dto;

import com.project.moyora.app.domain.*;
import lombok.*;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BoardDto {
    private Long id;
    private UserSummaryDto writer;
    private String title;
    private GenderType genderType;
    private Integer minAge;
    private Integer maxAge;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<TagDto> tags;
    private List<String> sub_tags;
    private String content;
    private Integer howMany;
    private Integer participation;
    private MeetType meetType;
    private String meetDetail;
    private LocalDateTime createdTime;
    private LocalDateTime updateTime;
    private ApplicationStatus userStatus;
    private Long applicationId;

    public BoardDto(Long id) {
        this.id = id;
    }

    public BoardDto(Board board, BoardApplication application) {
        this.id = board.getId();
        this.writer = new UserSummaryDto(board.getWriter()); // Board에 연결된 User 객체 사용
        this.title = board.getTitle();
        this.genderType = board.getGenderType();
        this.minAge = board.getMinAge();
        this.maxAge = board.getMaxAge();
        this.startDate = board.getStartDate();
        this.endDate = board.getEndDate();
        this.tags = board.getTags().stream().map(TagDto::from).collect(Collectors.toList());
        this.sub_tags = board.getSubTags().stream()
                .map(SubTag::getName)
                .collect(Collectors.toList());
        this.content = board.getContent();
        this.howMany = board.getHowMany();
        this.participation = board.getParticipation();
        this.meetType = board.getMeetType();
        this.meetDetail = board.getMeetDetail();
        this.createdTime = board.getCreatedTime();
        this.updateTime = board.getUpdateTime();
        if (application != null) {
            this.userStatus = application.getStatus();
            this.applicationId = application.getId();
        } else {
            this.userStatus = null;
            this.applicationId = null;
        }
    }

    public Board toEntity() {
        // 간단하게 필요한 필드만 복원
        Board board = new Board();
        board.setId(this.id);
        // 다른 필드도 필요 시 설정
        return board;
    }

}

