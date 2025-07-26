package com.juneyng.mungle.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class EmotionRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String text;
    private String emotion;
    private Double confidence;
    private String message;

    // 기본 생성자 (JPA 필요)
    public EmotionRecord() {}

    // getter와 setter
    public Long getId() { return id; }
    public String getText() { return text; }
    public String getEmotion() { return emotion; }
    public Double getConfidence() { return confidence; } // 신뢰도. 정확도를 뜻함
    public String getMessage() { return message; }

    public void setId(Long id) { this.id = id; }
    public void setText(String text) { this.text = text; }
    public void setEmotion(String emotion) { this.emotion = emotion; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }
    public void setMessage(String message) { this.message = message; }
}