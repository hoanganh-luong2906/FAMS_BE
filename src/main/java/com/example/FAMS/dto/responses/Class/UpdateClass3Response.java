package com.example.FAMS.dto.responses.Class;

import com.example.FAMS.models.TrainingProgramSyllabus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateClass3Response {

    private String status;
    private TrainingProgramSyllabus updatedClass3;
}
