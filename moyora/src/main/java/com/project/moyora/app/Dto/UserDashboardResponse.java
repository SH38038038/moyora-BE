package com.project.moyora.app.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserDashboardResponse {
    private List<InterestTagDto> interestTags;
    private List<BoardDto> likedBoards;
    private List<BoardDto> createdBoards;
    private List<BoardDto> participatingBoards;
}