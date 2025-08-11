package com.project.moyora.app.domain;

public enum NotificationType {
    // 모임 관련
    WAITING,         // 모임 글 신청
    REJECTED,        // 신청 거절
    ACCEPTED,        // 신청 수락
    CANCELED,
    CONFIRMED,       // 모임 확정
    ROOM_EXPLOSION,  // 모임 방 폭파 알림 (1주, 3일, 1일 전)

    // 채팅 관련
    CHAT_MESSAGE,
    CHAT_NOTICE,

    // 인증 관련
    ID_VERIFICATION_REJECTED,
    ID_VERIFICATION_ACCEPTED,

    // 신고 관련
    REPORT_RESULT,
    SUSPENSION,

    // 시스템 알림
    SYSTEM_ALERT
}
