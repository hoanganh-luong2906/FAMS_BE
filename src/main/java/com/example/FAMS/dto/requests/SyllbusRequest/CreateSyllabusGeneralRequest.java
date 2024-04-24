package com.example.FAMS.dto.requests.SyllbusRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateSyllabusGeneralRequest {

    String topicCode;
    String topicName;
    String version;
    String technicalRequirement;
    String priority;
    String courseObjective;
    String publishStatus = "inactive";
    String trainingPrinciple;
    int trainingAudience;


}

