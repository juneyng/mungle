package com.juneyng.mungle.service;

import com.juneyng.mungle.domain.EmotionRecord;
import com.juneyng.mungle.repository.EmotionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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
        repository.save(record);
    }

    public List<EmotionRecord> getAllRecords() {
        return repository.findAll();
    }

    public void deleteEmotion(Long id) {
        repository.deleteById(id);
    }
}