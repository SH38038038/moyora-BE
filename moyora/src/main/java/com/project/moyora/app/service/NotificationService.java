package com.project.moyora.app.service;


import com.project.moyora.app.domain.NotificationEntity;
import com.project.moyora.app.domain.NotificationType;
import com.project.moyora.app.dto.Notification;
import com.project.moyora.app.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SseEmitterService sseEmitterService;

    // 알림 저장
    public void sendNotification(Long userId, NotificationType type, String content) {
        // DB 저장
        NotificationEntity notification = NotificationEntity.builder()
                .userId(userId)
                .type(type)
                .content(content)
                .build();
        notificationRepository.save(notification);

        // 실시간 전송 (SSE)
        sseEmitterService.broadcast(userId, new Notification(type, content));
    }

    // 읽지 않은 알림 조회
    public List<NotificationEntity> getUnreadNotifications(Long userId) {
        return notificationRepository.findAllByUserIdAndReadFalseOrderByCreatedAtDesc(userId);
    }

    // 모두 읽음 처리 (삭제)
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsReadByUserId(userId);
    }
}
