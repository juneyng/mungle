package com.juneyng.mungle.controller;

import com.juneyng.mungle.service.EmotionService;
import com.juneyng.mungle.service.HuggingFaceEmotionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AnalyzeController {

    private final EmotionService emotionService;
    private final HuggingFaceEmotionService huggingFaceEmotionService;

    public AnalyzeController(EmotionService emotionService,
                             HuggingFaceEmotionService huggingFaceEmotionService) {
        this.emotionService = emotionService;
        this.huggingFaceEmotionService = huggingFaceEmotionService;
    }

    @PostMapping("/analyze")
    public Mono<ResponseEntity<Map<String, Object>>> analyzeText(@RequestBody Map<String, String> request) {
        String text = request.get("text");
        if (text == null || text.trim().isEmpty()) {
            Map<String, Object> errorResponse = Map.of("error", "텍스트를 입력해주세요.");
            return Mono.just(ResponseEntity.badRequest().body(errorResponse));
        }

        // HuggingFaceEmotionService를 사용하여 감정 분석 수행
        return huggingFaceEmotionService.analyzeText(text)
                .map(result -> ResponseEntity.ok(result))
                .onErrorReturn(ResponseEntity.ok(Map.of(
                        "emotion", "중립",
                        "message", "괜찮아요, 당신의 마음이 편안했으면 좋겠어요.",
                        "confidence", 0.0
                )));
    }

    @GetMapping("/emotions")
    public ResponseEntity<?> getEmotions() {
        return ResponseEntity.ok(emotionService.getEmotions());
    }

    @GetMapping("/test-ai")
    public Mono<ResponseEntity<?>> testAi(@RequestParam String text) {
        return huggingFaceEmotionService.analyzeText(text)
                .map(ResponseEntity::ok);
    }
}