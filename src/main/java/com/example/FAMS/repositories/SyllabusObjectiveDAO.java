package com.example.FAMS.repositories;

import com.example.FAMS.models.SyllabusObjective;
import com.example.FAMS.models.composite_key.SyllabusStandardOutputCompositeKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SyllabusObjectiveDAO extends JpaRepository<SyllabusObjective, SyllabusStandardOutputCompositeKey> {

    List<SyllabusObjective> findByTopicCode_TopicCode(String topicCode);
}
