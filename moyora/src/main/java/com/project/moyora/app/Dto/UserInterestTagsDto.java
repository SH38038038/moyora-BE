package com.project.moyora.app.dto;

import com.project.moyora.app.domain.SubTag;
import com.project.moyora.app.domain.Tag;
import com.project.moyora.global.tag.InterestTag;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class UserInterestTagsDto {
    private final List<Tag> interestTags;

    public UserInterestTagsDto(Set<SubTag> tags) {
        this.interestTags = tags.stream()
                .map(tag -> new Tag(tag.getId(), tag.getName()))
                .collect(Collectors.toList());
    }

    @Getter
    @AllArgsConstructor
    public static class Tag {
        private Long id;
        private String name;
    }
}
