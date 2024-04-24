package com.example.FAMS.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseTrainingProgram {
    private String status;
    private String message;
    private int numberOfPage;
    private Object payload;
//    private TrainingProgramSyllabus trainingProgramSyllabusList;


}
