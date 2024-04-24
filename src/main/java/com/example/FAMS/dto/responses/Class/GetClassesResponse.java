package com.example.FAMS.dto.responses.Class;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetClassesResponse {

    String className;
    String classCode;
    String createdBy;
    String duration;
    String status;
    String location;
    String fsu;
    String createdOn;
    String isDeactivated;
}
