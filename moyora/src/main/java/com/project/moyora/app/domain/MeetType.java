package com.project.moyora.app.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MeetType {
    ONLINE("ONLINE"),
    OFFLINE("OFFLINE");

    private final String key;
}
