package com.juneyng.mungle.service;

import com.juneyng.mungle.domain.EmotionRecord;
import com.juneyng.mungle.repository.EmotionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmotionService {

    private final EmotionRepository repository;

    public EmotionService(EmotionRepository repository) {
        this.repository = repository;
    }

    public void saveEmotion(String text, String emotion, Double confidence, String message) {
        EmotionRecord record = new EmotionRecord();
        record.setText(text);
        record.setEmotion(emotion);
        record.setConfidence(confidence);
        record.setMessage(message);
        record.setTimestamp(LocalDateTime.now()); // 날짜 자동 설정
        repository.save(record);
    }

    public List<EmotionRecord> getAllRecords() {
        return repository.findAll();
    }

    public void deleteEmotion(Long id) {
        repository.deleteById(id);
    }

    public Map<String, Long> getEmotionStatistics() {
        List<EmotionRecord> records = repository.findAll();
        Map<String, Long> stats = new HashMap<>();
        for (EmotionRecord record : records) {
            stats.merge(record.getEmotion(), 1L, Long::sum);
        }
        return stats;
    }

    public Map<LocalDateTime, Map<String, Long>> getDailyEmotionStatistics() {
        List<EmotionRecord> records = repository.findAll();
        Map<LocalDateTime, Map<String, Long>> dailyStats = new HashMap<>();
        for (EmotionRecord record : records) {
            LocalDateTime date = record.getTimestamp().toLocalDate().atStartOfDay();
            dailyStats.computeIfAbsent(date, k -> new HashMap<>())
                    .merge(record.getEmotion(), 1L, Long::sum);
        }
        return dailyStats;
    }
}