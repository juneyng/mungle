package com.juneyng.mungle.controller;

import com.juneyng.mungle.domain.EmotionRecord; // 추가
import com.juneyng.mungle.service.EmotionService;
import com.juneyng.mungle.service.HuggingFaceEmotionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
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

        return huggingFaceEmotionService.analyzeText(text)
                .doOnNext(result -> emotionService.saveEmotion(
                        text,
                        (String) result.get("emotion"),
                        (Double) result.get("confidence"),
                        (String) result.get("message")
                ))
                .map(ResponseEntity::ok)
                .onErrorReturn(ResponseEntity.ok(Map.of(
                        "emotion", "중립",
                        "confidence", 0.0,
                        "message", "네트워크 오류로 분석에 실패했어요. 중립으로 처리됩니다."
                )));
    }

    @GetMapping("/history")
    public ResponseEntity<List<EmotionRecord>> getHistory() { // 타입 명시 유지
        return ResponseEntity.ok(emotionService.getAllRecords());
    }

    @DeleteMapping("/history/{id}")
    public ResponseEntity<Void> deleteHistory(@PathVariable Long id) {
        emotionService.deleteEmotion(id);
        return ResponseEntity.noContent().build();
    }
}