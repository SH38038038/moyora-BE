package com.project.moyora.app.dto;

import com.project.moyora.app.domain.MeetType;
import com.project.moyora.global.tag.InterestTag;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class BoardSearchRequest {
    private String title;
    private List<InterestTag> interestTag;
    private MeetType meetType;
    private String meetDetail;

    private int page = 0;
    private int size = 10;
}
