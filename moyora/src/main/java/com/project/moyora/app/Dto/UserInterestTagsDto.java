package com.project.moyora.app.Dto;

import com.project.moyora.global.tag.InterestTag;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
public class UserInterestTagsDto {
    private Set<String> interestTags;

    public UserInterestTagsDto(Set<InterestTag> interestTags) {
        this.interestTags = interestTags.stream()
                .map(InterestTag::name)
                .collect(Collectors.toSet());
    }
}
