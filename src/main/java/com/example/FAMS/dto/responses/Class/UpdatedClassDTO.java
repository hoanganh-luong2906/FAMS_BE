package com.example.FAMS.dto.responses.Class;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdatedClassDTO {

    String classCode;
    String className;
    String totalLearningTime;
    String classTimeFrom;
    String classTimeTo;
    String status;
    String location;
    String fsu;
    String startDate;
    String endDate;
    String attendee;
    String attendeePlanned;
    String attendeeAccepted;
    String attendeeActual;
    List<String> listDay;
    List<String> attendeeList;
    List<String> adminList;
}
