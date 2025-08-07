package com.project.moyora.global.tag;

import java.util.Arrays;
import java.util.Optional;

public enum InterestTag {
    HOBBY("취미/여가"),
    SPORTS("운동/스포츠"),
    SELF_IMPROVEMENT("자기계발"),
    SOCIAL("소셜/네트워킹"),
    CULTURE_ART("문화/예술"),
    TRAVEL_OUTDOOR("여행/아웃도어");

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
