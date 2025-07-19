package com.project.moyora.app.service;


import com.project.moyora.app.domain.NotificationType;
import com.project.moyora.app.dto.Notification;
import com.project.moyora.app.repository.EmitterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SseEmitterService {

    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;
    private final EmitterRepository emitterRepository;

    public SseEmitter subscribe(Long userId) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);

        // 저장
        emitterRepository.save(userId, emitter);

        // 삭제 이벤트 등록
        emitter.onCompletion(() -> emitterRepository.deleteEmitter(userId, emitter));
        emitter.onTimeout(() -> emitterRepository.deleteEmitter(userId, emitter));

        // 연결 확인용 이벤트 전송
        Notification notification = new Notification(NotificationType.SYSTEM_ALERT, "SSE 연결 완료");
        sendToClient(userId, notification);

        return emitter;
    }


    public void broadcast(Long userId, Notification notification) {
        sendToClient(userId, notification);
    }

    private void sendToClient(Long userId, Object data) {
        List<SseEmitter> emitterList = emitterRepository.findEmittersByUserId(userId);

        if (emitterList.isEmpty()) {
            return; // 연결된 클라이언트 없음
        }

        MediaType mediaTypeUtf8 = new MediaType("application", "json", StandardCharsets.UTF_8);

        for (SseEmitter emitter : emitterList) {
            try {
                emitter.send(
                        SseEmitter.event()
                                .id(userId.toString())
                                .name("sse")
                                .data(data, mediaTypeUtf8)
                );
            } catch (IOException e) {
                emitterRepository.deleteEmitter(userId, emitter);
            }
        }
    }}
