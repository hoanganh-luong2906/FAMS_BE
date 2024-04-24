package com.example.FAMS.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSyllabusRequest {

    private String topicCode;
    private String topicName;
    private String technicalGroup;
    private String version;
    private int trainingAudience;
    private String topicOutline;
    //    private Set<TrainingMaterial> trainingMaterials;
    private String trainingPrinciples;
    private String priority;
    private String publishStatus;
    private String createdBy;
    private Date createdDate;
    private String modifiedBy;
    private Date modifiedDate;

}
