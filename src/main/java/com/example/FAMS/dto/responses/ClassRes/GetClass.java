package com.example.FAMS.dto.responses.ClassRes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetClass {

    String className;
    String classCode;
    Long createdOn;
    String createBy;
    int duration;
    String status;
    String location;
    String fsu;
}
