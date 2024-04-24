package com.example.FAMS.dto.responses;

import com.example.FAMS.models.TrainingProgramSyllabus;
import com.example.FAMS.models.User;

import java.util.Date;
import java.util.Set;

public interface TrainingProgramModified {
    int getTrainingProgramCode();

    String getName();

    int getDuration();

    String getStatus();

    String getCreatedBy();

    Date getCreatedDate();

    Set<TrainingProgramSyllabus> getTrainingProgramSyllabus();

    User getUserID();
}
