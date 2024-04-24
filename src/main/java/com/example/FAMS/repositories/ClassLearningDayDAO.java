package com.example.FAMS.repositories;

import com.example.FAMS.models.Class;
import com.example.FAMS.models.ClassLearningDay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface ClassLearningDayDAO extends JpaRepository<ClassLearningDay, Integer> {

    ClassLearningDay findByClassIdAndEnrollDate(Class lop, Date enrollDate);

    ClassLearningDay findByIdAndAndEnrollDate(int id, Date enrollDate);

    List<ClassLearningDay> findByClassId_ClassId(String id);

//    List<ClassLearningDay> findByClassId_ClassIdAndLocationId_LocationId(String classId, Long locationId);
}
