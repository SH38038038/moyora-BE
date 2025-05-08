package com.project.moyora.app.Dto;

import com.project.moyora.app.domain.MeetType;
import com.project.moyora.global.tag.InterestTag;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BoardListDto {
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private MeetType meetType;
    private String meetDetail;
    private InterestTag interestTag;
    private Integer howMany;
    private Integer participation;
    private String detailUrl;

    private boolean liked;
}