package com.project.moyora.app.Dto;

import com.project.moyora.app.domain.User;
import com.project.moyora.global.tag.InterestTag;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

import static org.yaml.snakeyaml.tokens.Token.ID.Tag;
public class UserResponseDto {

    private String email;
    private String nickname;
    private List<InterestTagDto> interestTags;

    public UserResponseDto(User user) {
        this.email = user.getEmail();
        this.nickname = user.getName();
        this.interestTags = user.getInterestTags().stream()
                .map(InterestTagDto::from)
                .collect(Collectors.toList());
    }

    @Getter
    @AllArgsConstructor
    public static class InterestTagDto {
        private String section;
        private String name;

        public static InterestTagDto from(InterestTag tag) {
            return new InterestTagDto(tag.getSection(), tag.getDisplayName());
        }
    }
}
