package com.example.FAMS.dto.responses.ClassRes;

import com.example.FAMS.models.Syllabus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetClassDetail {

    String className;
    String classCode;
    Long createdOn;
    String createBy;
    int duration;
    String status;
    String location;
    String fsu;
    Time timeFrom;
    Time timeTo;
    String attendee;
    int attendeePlanned;
    int attendeeAccepted;
    int attendeeActual;
    Long startDate;
    Long endDate;
    String trainingProgram;
    Long trainingProgramModifiedDate;
    String trainingProgramModifiedBy;
    List<Syllabus> listOfSyllabus;
}
