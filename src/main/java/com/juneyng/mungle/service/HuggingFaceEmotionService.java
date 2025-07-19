package com.juneyng.mungle.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HuggingFaceEmotionService {

    private final WebClient webClient;

    @Value("${huggingface.api.key}")
    private String apiKey;

    public HuggingFaceEmotionService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://api-inference.huggingface.co/models/mrm8488/bert-tiny-finetuned-sst2")
                .build();
    }

    public Mono<Map<String, Object>> analyzeText(String text) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("inputs", text);

        return webClient.post()
                .header("Authorization", "Bearer " + apiKey)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                .map(response -> {
                    if (response == null || response.isEmpty()) {
                        Map<String, Object> defaultMap = new HashMap<>();
                        defaultMap.put("emotion", "중립");
                        defaultMap.put("message", "분석할 데이터가 없습니다.");
                        return defaultMap;
                    }

                    Map<String, Object> result = response.get(0);
                    String label = (String) result.get("label");
                    double score = (double) result.get("score");
                    String emotion = label.equals("POSITIVE") ? "기쁨" : "슬픔";

                    Map<String, Object> output = new HashMap<>();
                    output.put("emotion", emotion);
                    output.put("confidence", score);
                    return output;
                })
                .onErrorResume(e -> {
                    Map<String, Object> errorMap = new HashMap<>();
                    errorMap.put("emotion", "중립");
                    errorMap.put("message", "분석 중 오류가 발생했어요.");
                    return Mono.just(errorMap);
                });
    }
}
