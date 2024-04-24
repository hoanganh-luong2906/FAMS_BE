package com.example.FAMS.repositories;

import com.example.FAMS.models.TrainingMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainingMaterialDAO extends JpaRepository<TrainingMaterial, Integer> {

//    @Query("""
//                SELECT TM FROM TrainingMaterial AS TM WHERE TM.syllabus.topicCode = :topicCode
//            """)
//    List<TrainingMaterial> findTrainingMaterialBySyllabus(String topicCode);

    List<TrainingMaterial> findByTrainingContent_UnitCode_Syllabus_TopicCode(String topicCode);

}
