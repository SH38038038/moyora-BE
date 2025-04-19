package com.project.moyora.app.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GenderType {
    MALE("Male"),
    FEMALE("Female"),
    OTHER("Other");

    private final String key;

}
