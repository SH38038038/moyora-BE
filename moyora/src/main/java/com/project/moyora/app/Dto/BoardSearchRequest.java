package com.project.moyora.app.Dto;

import com.project.moyora.app.domain.MeetType;
import com.project.moyora.global.tag.InterestTag;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardSearchRequest {
    private String title; // 키워드 검색
    private InterestTag interestTag; // 관심사 태그
    private MeetType meetType; // 온라인/오프라인
}
