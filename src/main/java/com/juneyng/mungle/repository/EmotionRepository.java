package com.juneyng.mungle.repository;

import com.juneyng.mungle.domain.EmotionRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmotionRepository extends JpaRepository<EmotionRecord, Long> {
}