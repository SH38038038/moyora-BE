package com.project.moyora.app.domain;

public enum ApplicationStatus {
    WAITING,    // 신청 대기
    ACCEPTED,   // 작성자 수락
    REJECTED,   // 작성자 거절
    CANCELED,   // 신청자 신청 취소 (확정 후 취소 불가)
    LOCKED      // 모임 확정 (취소 불가, 거절 불가)
}

