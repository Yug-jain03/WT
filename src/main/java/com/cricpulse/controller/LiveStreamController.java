package com.cricpulse.controller;

import com.cricpulse.service.SseService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@CrossOrigin(origins = "*")
public class LiveStreamController {

    private final SseService sseService;

    public LiveStreamController(SseService sseService) {
        this.sseService = sseService;
    }

    /**
     * Server-Sent Events (SSE) live stream endpoint for instant dashboard updates.
     */
    @GetMapping(value = "/api/live", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamLiveUpdates() {
        return sseService.createEmitter();
    }
}
