package com.project.moyora.app.controller;

import com.project.moyora.app.domain.NotificationEntity;
import com.project.moyora.app.domain.User;
import com.project.moyora.global.security.CustomUserDetails;
import com.project.moyora.app.dto.Notification;
import com.project.moyora.app.repository.EmitterRepository;
import com.project.moyora.app.service.NotificationService;
import com.project.moyora.app.service.SseEmitterService;
import com.project.moyora.global.exception.InvalidTokenException;
import com.project.moyora.global.security.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notification")
public class NotificationController {

    private final SseEmitterService sseEmitterService;
    private final NotificationService notificationService;
    private final TokenService tokenService;
    private final EmitterRepository emitterRepository;

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@RequestParam String token) {
        if (!tokenService.validateToken(token)) {
            throw new InvalidTokenException("유효하지 않은 토큰입니다.");
        }

        Long userId = tokenService.extractUserId(token)
                .orElseThrow(() -> new InvalidTokenException("토큰에서 사용자 ID를 찾을 수 없습니다."));
        log.info("SSE 구독: userId={}, 현재 연결 수={}", userId, emitterRepository.findEmittersByUserId(userId).size());

        return sseEmitterService.subscribe(userId);
    }

    @PostMapping("/broadcast")
    public void broadcast(@AuthenticationPrincipal CustomUserDetails userDetails,
                          @RequestBody Notification notification) {
        User user = userDetails.getUser();
        Long userId = user.getId();
        // sseEmitterService.broadcast(userId, notification);
        notificationService.sendNotification(userId, notification.getType(), notification.getContent());
    }

    @GetMapping("/unread")
    public List<NotificationEntity> getUnreadNotifications(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        Long userId = user.getId();
        return notificationService.getUnreadNotifications(userId);
    }

    @PostMapping("/mark-all-read")
    public ResponseEntity<?> markAllAsRead(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        Long userId = user.getId();
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }

}
