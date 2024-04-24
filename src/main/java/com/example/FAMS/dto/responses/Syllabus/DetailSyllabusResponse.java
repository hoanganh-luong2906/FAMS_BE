package com.example.FAMS.dto.responses.Syllabus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DetailSyllabusResponse {

    String topicCode;
    String topicName;
    int numberOfDay = 1;
    String technicalGroup;
    String version;
    int trainingAudience;
    String topicOutline;
    String trainingPrinciples;
    String priority;
    String publishStatus;
    Date createdDate;
    String modifiedBy;
    String courseObjective;
    boolean deleted = false;
}
