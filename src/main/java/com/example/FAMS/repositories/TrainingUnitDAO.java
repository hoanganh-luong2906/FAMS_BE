package com.example.FAMS.repositories;

import com.example.FAMS.models.TrainingUnit;
import com.example.FAMS.models.composite_key.SyllabusTrainingUnitCompositeKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainingUnitDAO extends JpaRepository<TrainingUnit, SyllabusTrainingUnitCompositeKey> {

    int countDayNumberBySyllabus_TopicCode(String topicCode);

    List<TrainingUnit> findBySyllabus_TopicCode(String topicCode);
}
