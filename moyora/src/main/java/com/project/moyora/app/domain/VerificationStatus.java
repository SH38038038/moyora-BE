package com.project.moyora.app.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VerificationStatus {
    PENDING("pending"),
    ACCEPTED("accepted"),
    REJECTED("rejected");

    private final String key;
}
