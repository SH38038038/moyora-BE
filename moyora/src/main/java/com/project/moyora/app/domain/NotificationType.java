package com.project.moyora.app.domain;

public enum NotificationType {
    LETTER,             // 쪽지 알림
    TEAM_EXPIRATION,     // 팀 유효기간 알림
    APPOINTMENT_CREATED, // 약속 잡기 생성 알림
    APPOINTMENT_COMPLETED, // 약속 완료 알림
    NEW_MEMO,            // 신규 메모 알림
    BALANCE_GAME_CREATED, // 밸런스 게임 생성 알림
    SYSTEM_ALERT    // 시스템 알림
}
