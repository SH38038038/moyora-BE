package com.project.moyora.app.service;

import com.project.moyora.app.Dto.InterestTagDto;
import com.project.moyora.app.Dto.InterestTagGroupDto;
import com.project.moyora.app.domain.Tag;
import com.project.moyora.global.tag.InterestTag;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.LinkedHashMap;

@Service
public class InterestTagService {

    public List<InterestTagGroupDto> getGroupedInterestTags() {
        Map<String, List<Tag>> grouped = Arrays.stream(InterestTag.values())
                .map(tag -> new Tag(tag.getSection(), tag.name(), tag.getDisplayName()))
                .collect(Collectors.groupingBy(Tag::getSection));

        return grouped.entrySet().stream()
                .map(entry -> new InterestTagGroupDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

}
