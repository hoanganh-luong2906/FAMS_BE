package com.example.FAMS.repositories;

import com.example.FAMS.models.ClassUser;
import com.example.FAMS.models.composite_key.ClassUserCompositeKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClassUserDAO extends JpaRepository<ClassUser, ClassUserCompositeKey> {

    List<ClassUser> findByClassId_ClassId(String classId);

    List<ClassUser> findByClassId_ClassIdAndUserType(String classId, String userType);
}
