package com.example.FAMS.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseObjectVersion2 {
    private String status;
    private String message;
    private int totalSubjectDays;
    private int totalTrainingProgramDates;
    private Object payload;


}
