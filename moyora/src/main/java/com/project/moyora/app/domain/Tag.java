package com.project.moyora.app.domain;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Tag {
    private String name;
    private String displayName;

    public Tag(String name, String displayName) {

        this.name = name;
        this.displayName = displayName;
    }
}
