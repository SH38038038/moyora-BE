package com.project.moyora.app.dto;

import com.project.moyora.app.domain.User;
import com.project.moyora.global.tag.InterestTag;
import java.util.List;

public record UserSimpleDto(
        String nickname,
        String email,
        List<String> interestTags // enum 이름 또는 displayName으로 변환
) {
    public static UserSimpleDto from(User user) {
        return new UserSimpleDto(
                user.getName(),
                user.getEmail(),
                user.getInterestTags().stream()
                        .map(InterestTag::name) // 또는 .map(InterestTag::getDisplayName)
                        .toList()
        );
    }
}
