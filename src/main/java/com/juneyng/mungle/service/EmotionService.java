package com.juneyng.mungle.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class EmotionService {
    private List<Map<String, Object>> emotions;

    @PostConstruct
    public void init() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ClassPathResource resource = new ClassPathResource("emotions.json");
        emotions = mapper.readValue(resource.getInputStream(), List.class);
        System.out.println("Loaded emotions: " + emotions.size());
    }

    public List<Map<String, Object>> getEmotions() {
        return emotions;
    }

    public Map<String, Object> analyzeText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }

        text = text.toLowerCase(); // 대소문자 무시
        for (Map<String, Object> emotion : emotions) {
            List<String> keywords = (List<String>) emotion.get("keywords");
            for (String keyword : keywords) {
                if (text.contains(keyword.toLowerCase())) {
                    return emotion; // 키워드 매칭 시 해당 감정 반환
                }
            }
        }
        return null; // 매칭되지 않으면 null 반환 (나중에 중립 처리 가능)
    }

    public String getRandomSentence(Map<String, Object> emotion) {
        if (emotion == null) return null;
        List<String> sentences = (List<String>) emotion.get("sentences");
        if (sentences == null || sentences.isEmpty()) return null;
        int index = (int) (Math.random() * sentences.size());
        return sentences.get(index); // 무작위 위로 문장 반환
    }
}