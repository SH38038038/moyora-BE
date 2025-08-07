package com.project.moyora.app.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.project.moyora.app.domain.SubTag;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubTagDto {
    // private Long id;
    private String name;

    public static SubTagDto from(SubTag subTag) {
        return SubTagDto.builder()
                // .id(subTag.getId())
                .name(subTag.getName())
                .build();
    }
}

