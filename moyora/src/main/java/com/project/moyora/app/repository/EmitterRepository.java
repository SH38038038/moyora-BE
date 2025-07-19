package com.project.moyora.app.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class EmitterRepository {

    // 하나의 userId에 여러 개의 emitter 연결을 허용
    private final Map<Long, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public List<SseEmitter> findEmittersByUserId(Long userId) {
        return emitters.getOrDefault(userId, new ArrayList<>());
    }

    public void save(Long userId, SseEmitter emitter) {
        emitters.computeIfAbsent(userId, key -> new ArrayList<>()).add(emitter);
    }

    public void deleteByUserId(Long userId) {
        emitters.remove(userId);
    }

    public void deleteEmitter(Long userId, SseEmitter emitter) {
        List<SseEmitter> emitterList = emitters.get(userId);
        if (emitterList != null) {
            emitterList.remove(emitter);
        }
    }
}
