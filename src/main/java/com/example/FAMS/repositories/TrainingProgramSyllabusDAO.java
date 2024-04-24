package com.example.FAMS.repositories;

import com.example.FAMS.models.TrainingProgramSyllabus;
import com.example.FAMS.models.composite_key.SyllabusTrainingProgramCompositeKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainingProgramSyllabusDAO extends JpaRepository<TrainingProgramSyllabus, SyllabusTrainingProgramCompositeKey> {
    TrainingProgramSyllabus findByIdTopicCodeAndIdTrainingProgramCode(String topicCode, int trainingProgramCode);


}
