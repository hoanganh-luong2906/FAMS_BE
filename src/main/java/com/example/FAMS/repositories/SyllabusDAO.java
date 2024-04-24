package com.example.FAMS.repositories;

import com.example.FAMS.models.Syllabus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SyllabusDAO extends JpaRepository<Syllabus, String> {

    List<Syllabus> findTop1000ByOrderByCreatedDateDesc();

    List<Syllabus> findTop1000ByDeletedOrderByCreatedDateDesc(boolean deleted);
//    List<Syllabus> findTop1000ByDeletedFalseOrderByCreatedDateDesc();

    List<Syllabus> findAllByOrderByCreatedDateDesc();

    int countByTopicCodeLike(String topicCode);
//    @Query(value = "SELECT top 1 * from syllabus where topic_code like %:topicCode order by topic_code desc",nativeQuery = true)
//    Syllabus getLastSyllabusByTopicCode(String topicCode);

    int countByTopicNameLike(String topicName);

    List<Syllabus> findAllByPublishStatus(String status);

    Optional<Syllabus> findByTopicName(String topicName);


}
