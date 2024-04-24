package com.example.FAMS.dto.responses;

import com.example.FAMS.models.Class;
import com.example.FAMS.models.TrainingProgramSyllabus;
import com.example.FAMS.models.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingProgramDTOV2 {
    private int trainingProgramCode;

    private String name;

    private User userID;

    private Date startDate;

    private int duration;

    private String status;

    private String createdBy;

    private Date createdDate;

    private String modifiedBy = "";

    private Date modifiedDate = null;

    private Set<TrainingProgramSyllabus> trainingProgramSyllabus = new HashSet<>();

    private Set<Class> classes = new HashSet<>();

}
