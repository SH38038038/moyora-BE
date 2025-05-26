package com.project.moyora.app.Dto;

import com.project.moyora.app.domain.MeetType;
import com.project.moyora.global.tag.InterestTag;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardSearchRequest {
    private String title; // 키워드 검색 : 필수
    private InterestTag interestTag; // 관심사 태그 : 선택
    private MeetType meetType; // 온라인/오프라인 : 선택
    private String meetDetail; // 상세 만남 방식 : 선택
}
