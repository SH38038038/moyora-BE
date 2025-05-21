package com.project.moyora.app.domain;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Tag {
    private String section;
    private String name;
    private String displayName;

    public Tag(String section, String name, String displayName) {
        this.section = section;
        this.name = name;
        this.displayName = displayName;
    }
}
