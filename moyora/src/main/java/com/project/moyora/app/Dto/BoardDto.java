package com.project.moyora.app.Dto;

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
    private String content;
    private Integer howMany;
    private Integer participation;
    private MeetType meetType;
    private String meetDetail;
    private LocalDateTime createdTime;
    private LocalDateTime updateTime;
    private ApplicationStatus userStatus;

    public BoardDto(Long id) {
        this.id = id;
    }

    public BoardDto(Board board, BoardApplication boardApplication) {
        this.id = board.getId();
        this.writer = new UserSummaryDto(board.getWriter()); // Board에 연결된 User 객체 사용
        this.title = board.getTitle();
        this.genderType = board.getGenderType();
        this.minAge = board.getMinAge();
        this.maxAge = board.getMaxAge();
        this.startDate = board.getStartDate();
        this.endDate = board.getEndDate();
        this.tags = board.getTags().stream().map(TagDto::from).collect(Collectors.toList());
        this.content = board.getContent();
        this.howMany = board.getHowMany();
        this.participation = board.getParticipation();
        this.meetType = board.getMeetType();
        this.meetDetail = board.getMeetDetail();
        this.createdTime = board.getCreatedTime();
        this.updateTime = board.getUpdateTime();
        this.userStatus = boardApplication != null ? boardApplication.getStatus() : null;
    }
}

