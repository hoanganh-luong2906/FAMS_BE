package com.example.FAMS.repositories;

import com.example.FAMS.models.UserClassSyllabus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserClassSyllabusDAO extends JpaRepository<UserClassSyllabus, Integer> {

    List<UserClassSyllabus> findByClassCode_ClassId(String classCode);

    void deleteByClassCode_ClassId(String classCode);

    List<?> findDistinctByClassCode_ClassId(String classCode);
}
