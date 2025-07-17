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
}