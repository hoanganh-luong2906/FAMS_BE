package com.example.FAMS.dto.responses.Class;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SyllabusDTO {

    private String topicCode;

    private String topicName;

    private int numberOfDay = 1;

    private String version;

    private String publishStatus;

    private String createdBy;

    private String createdDate;
}
