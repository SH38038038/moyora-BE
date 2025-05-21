package com.project.moyora.app.Dto;

import com.project.moyora.app.domain.GenderType;
import com.project.moyora.app.domain.MeetType;
import com.project.moyora.global.tag.InterestTag;
import lombok.*;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BoardDto {
    private Long id;
    private String writer;
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

    public BoardDto(Long id) {
        this.id = id;
    }
}

