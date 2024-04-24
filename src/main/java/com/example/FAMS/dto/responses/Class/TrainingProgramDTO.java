package com.example.FAMS.dto.responses.Class;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TrainingProgramDTO {

    int trainingProgramCode;
    String trainingProgramName;
    String modifyDate;
    String modifyBy;
    Date startDate;
    int duration;
    String status;

}
