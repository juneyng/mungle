package com.juneyng.mungle.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AnalyzeController {
    @PostMapping("/analyze")
    public ResponseEntity<?> analyzeText(@RequestBody Map<String, String> request) {
        String text = request.get("text");
        if (text == null || text.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "텍스트를 입력해주세요."));
        }
        // 임시 응답 (나중에 감정 분석 로직 추가)
        return ResponseEntity.ok(Map.of(
                "emotion", "슬픔",
                "message", "괜찮아요, 당신은 충분히 잘하고 있어요."
        ));
    }
}