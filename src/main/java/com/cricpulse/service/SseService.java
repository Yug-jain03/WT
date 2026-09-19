package com.cricpulse.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class SseService {

    private static final Logger log = LoggerFactory.getLogger(SseService.class);
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SseEmitter createEmitter() {
        // Set timeout to 0L for indefinitely open connection
        SseEmitter emitter = new SseEmitter(0L);

        emitters.add(emitter);

        emitter.onCompletion(() -> {
            log.debug("SSE client completed connection");
            emitters.remove(emitter);
        });

        emitter.onTimeout(() -> {
            log.debug("SSE client connection timed out");
            emitters.remove(emitter);
        });

        emitter.onError(e -> {
            log.debug("SSE client connection error: {}", e.getMessage());
            emitters.remove(emitter);
        });

        // Send initial connected event to client
        try {
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data("{\"status\":\"CONNECTED\",\"message\":\"CricPulse Live SSE stream active\"}"));
        } catch (IOException e) {
            log.warn("Failed to send initial SSE connected event: {}", e.getMessage());
            emitters.remove(emitter);
        }

        return emitter;
    }

    public void broadcastScoreUpdate(Long matchId) {
        log.info("Broadcasting score-update event for matchId: {}", matchId);
        List<SseEmitter> deadEmitters = new CopyOnWriteArrayList<>();

        String payload = String.format("{\"matchId\":%d,\"timestamp\":%d}", matchId, System.currentTimeMillis());

        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("score-update")
                        .data(payload));
            } catch (Exception e) {
                log.debug("Failed sending update to emitter, queuing for removal");
                deadEmitters.add(emitter);
            }
        }

        emitters.removeAll(deadEmitters);
    }

    @Scheduled(fixedRate = 25000)
    public void sendHeartbeat() {
        if (emitters.isEmpty()) {
            return;
        }

        List<SseEmitter> deadEmitters = new CopyOnWriteArrayList<>();
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("ping")
                        .data("{\"type\":\"HEARTBEAT\"}"));
            } catch (Exception e) {
                deadEmitters.add(emitter);
            }
        }
        emitters.removeAll(deadEmitters);
    }
}
