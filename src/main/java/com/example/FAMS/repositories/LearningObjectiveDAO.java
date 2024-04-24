//package com.example.FAMS.repositories;
//
//import com.example.FAMS.models.LearningObjective;
//import com.example.FAMS.models.composite_key.TrainingContentLearningObjectiveCompositeKey;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//
//import java.util.List;
//
//public interface LearningObjectiveDAO extends JpaRepository<LearningObjective, TrainingContentLearningObjectiveCompositeKey> {
//
////    Optional<LearningObjective> findByLearningObjectiveCode(String s);
//
////    List<Object[]> findDistinctLearningObjectiveCodeByTopicCode_TopicCode(String topicCode);
//
////    @Query("SELECT DISTINCT lo.outputCode FROM LearningObjective lo WHERE lo.topicCode.topicCode = :topicCode")
////    List<String> findDistinctOutputCodeByTopicCode(@Param("topicCode") String topicCode);
//}
