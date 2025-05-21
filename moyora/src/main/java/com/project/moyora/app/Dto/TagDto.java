package com.project.moyora.app.Dto;

import com.project.moyora.global.tag.InterestTag;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TagDto {
    private String section;
    private String name;
    private String displayName;

    // 생성자
    public TagDto(String section, String name, String displayName) {
        this.section = section;
        this.name = name;
        this.displayName = displayName;
    }

    public static TagDto from(InterestTag tag) {
        return new TagDto(
                tag.getSection(),
                tag.name(),
                tag.getDisplayName()
        );
    }
}
