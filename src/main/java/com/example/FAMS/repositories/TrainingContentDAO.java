package com.example.FAMS.repositories;

import com.example.FAMS.models.TrainingContent;
import com.example.FAMS.models.composite_key.SyllabusTrainingUnitTrainingContentCompositeKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainingContentDAO extends JpaRepository<TrainingContent, SyllabusTrainingUnitTrainingContentCompositeKey> {

//    TrainingContent findByContentIdAndUnitCode_Id(int contentId, SyllabusTrainingUnitCompositeKey id);

    List<TrainingContent> findByUnitCode_Syllabus_TopicCode(String topicCode);
}
