package com.project.moyora.app.Dto;

import com.project.moyora.app.domain.GenderType;
import com.project.moyora.global.tag.InterestTag;
import lombok.*;

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
    private Integer Age;
    private LocalDate startDate;
    private LocalDate endDate;
    private InterestTag interestTag;
    private String content;
    private Integer howMany;
    private Integer participation;
    private LocalDateTime createdTime;
    private LocalDateTime updateTime;
}

