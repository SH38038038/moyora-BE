package com.project.moyora.app.dto;

import com.project.moyora.app.domain.Tag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class InterestTagGroupDto {
    private String section;
    private List<Tag> tags;
}
