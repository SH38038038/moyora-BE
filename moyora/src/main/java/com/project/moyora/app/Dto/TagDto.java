package com.project.moyora.app.dto;

import com.project.moyora.global.tag.InterestTag;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
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
        if (tag == null) {
            System.out.println("[DEBUG] TagDto.from() 호출 시 tag가 null입니다!");
            throw new IllegalArgumentException("InterestTag 값이 null입니다.");
        } else {
            System.out.println("[DEBUG] TagDto.from() 호출 시 tag = " + tag.name());
        }
        return TagDto.builder()
                .section(tag.getSection())
                .name(tag.name())
                .displayName(tag.getDisplayName())
                .build();
    }

    public TagDto() {

    }



}
