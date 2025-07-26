package com.project.moyora.global.tag;

import java.util.Arrays;
import java.util.Optional;

public enum InterestTag {
    SPORTS("스포츠"),
    MUSIC_APPRECIATION("음악감상"),
    IDOL("아이돌"),
    GAME("게임"),
    TRAVEL("여행"),
    OUTDOOR("아웃도어"),
    DRAWING("그림"),
    PHOTOGRAPHY("사진"),
    WRITING("글쓰기"),
    VIDEO_CONTENT("영상 콘텐츠"),
    STUDY("공부"),
    SELF_IMPROVEMENT("자기계발"),
    FOOD_EXPLORATION("맛집탐방"),
    DAILY_SHARING("일상공유"),
    FINANCE("재테크");

    private final String displayName;

    InterestTag(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Optional<InterestTag> fromName(String name) {
        return Arrays.stream(values())
                .filter(tag -> tag.name().equalsIgnoreCase(name))
                .findFirst();
    }
}
