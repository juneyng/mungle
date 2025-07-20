package com.juneyng.mungle.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Hugging Face API를 통해 영어 감정 분석을 수행하는 서비스 클래스.
 * j-hartmann/emotion-english-distilroberta-base 모델을 사용.
 */
@Service
public class HuggingFaceEmotionService {

    private final WebClient webClient;

    @Value("${huggingface.api.key}")
    private String apiKey;

    public HuggingFaceEmotionService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://api-inference.huggingface.co/models/j-hartmann/emotion-english-distilroberta-base")
                .build();
    }

    public Mono<Map<String, Object>> analyzeText(String text) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("inputs", text != null ? text : "");

        return webClient.post()
                .header("Authorization", "Bearer " + apiKey)
                .bodyValue(requestBody)
                .retrieve()
                // 중첩 배열 구조로 변경: List<List<Map<String, Object>>>
                .bodyToMono(new ParameterizedTypeReference<List<List<Map<String, Object>>>>() {})
                .map(this::processResponse)
                .onErrorResume(this::handleError);
    }

    // 중첩 배열 처리로 메서드 시그니처 변경
    private Map<String, Object> processResponse(List<List<Map<String, Object>>> response) {
        System.out.println("Raw Response: " + response);
        if (response == null || response.isEmpty() || response.get(0).isEmpty()) {
            return createDefaultResponse("분석할 데이터가 없습니다.");
        }

        // 첫 번째 배열의 첫 번째 결과에서 가장 높은 확률의 감정 찾기
        List<Map<String, Object>> emotions = response.get(0);
        Map<String, Object> highestEmotion = emotions.stream()
                .max((e1, e2) -> {
                    Double score1 = getDoubleValue(e1, "score");
                    Double score2 = getDoubleValue(e2, "score");
                    return Double.compare(score1 != null ? score1 : 0.0, score2 != null ? score2 : 0.0);
                })
                .orElse(null);

        if (highestEmotion == null) {
            return createDefaultResponse("감정 라벨을 인식하지 못했어요.");
        }

        String label = getStringValue(highestEmotion, "label");
        Double score = getDoubleValue(highestEmotion, "score");

        if (label == null) {
            return createDefaultResponse("감정 라벨을 인식하지 못했어요.");
        }

        String emotion = mapEmotionToEnglish(label);
        String comfortMessage = generateComfortMessage(emotion, score != null ? score : 1.0);

        Map<String, Object> output = new HashMap<>();
        output.put("emotion", emotion);
        output.put("confidence", score != null ? score : 1.0);
        output.put("message", comfortMessage);
        output.put("originalLabel", label);
        output.put("allEmotions", emotions); // 디버깅용으로 모든 감정 결과 포함
        return output;
    }

    private Mono<Map<String, Object>> handleError(Throwable e) {
        e.printStackTrace();
        Map<String, Object> errorMap = new HashMap<>();
        errorMap.put("emotion", "중립");
        errorMap.put("confidence", 0.0);
        errorMap.put("message", "괜찮아요, 지금 이 순간도 충분히 소중해요.");
        errorMap.put("error", e.getMessage());
        return Mono.just(errorMap);
    }

    private Map<String, Object> createDefaultResponse(String message) {
        Map<String, Object> defaultMap = new HashMap<>();
        defaultMap.put("emotion", "중립");
        defaultMap.put("message", message);
        defaultMap.put("confidence", 0.0);
        return defaultMap;
    }

    private String getStringValue(Map<String, Object> map, String key) {
        return map != null && map.containsKey(key) ? (String) map.get(key) : null;
    }

    private Double getDoubleValue(Map<String, Object> map, String key) {
        Object value = map != null && map.containsKey(key) ? map.get(key) : null;
        if (value == null) return null;
        if (value instanceof Double) return (Double) value;
        if (value instanceof Number) return ((Number) value).doubleValue();
        return null;
    }

    private String mapEmotionToEnglish(String label) {
        if (label == null) return "neutral";

        return switch (label.toLowerCase()) {
            case "anger" -> "anger";
            case "disgust" -> "disgust";
            case "fear" -> "fear";
            case "joy" -> "joy";
            case "neutral" -> "neutral";
            case "sadness" -> "sadness";
            case "surprise" -> "surprise";
            default -> label;
        };
    }

    private String generateComfortMessage(String emotion, double confidence) {
        if (confidence < 0.7) {
            return "지금 느끼는 감정이 무엇이든, 그대로의 당신이 소중해요.";
        }

        return switch (emotion) {
            case "joy" -> "좋은 기운이 느껴져요! 이런 순간들이 계속 이어지길 바라요 ✨";
            case "sadness" -> "힘든 시간을 보내고 계시는군요. 혼자가 아니니까 천천히 괜찮아지실 거예요 💙";
            case "anger" -> "화가 나는 일이 있으셨군요. 감정을 표현하는 것도 필요해요. 깊게 숨을 쉬어보세요 🌱";
            case "fear" -> "무서운 마음이 드시는군요. 안전한 곳에 있으니까 천천히 마음을 가라앉혀보세요 🛡️";
            case "disgust" -> "불쾌한 기분이 드셨군요. 마음을 정리할 시간을 가져보세요 🌿";
            case "surprise" -> "놀라운 순간이시군요! 긍정적인 변화가 되길 바랍니다 🌟";
            case "neutral" -> "마음이 차분한 상태네요. 이런 평온한 순간도 소중해요 🕊️";
            default -> "지금 이 순간의 감정을 느끼고 계시는군요. 어떤 감정이든 괜찮아요 💝";
        };
    }
}