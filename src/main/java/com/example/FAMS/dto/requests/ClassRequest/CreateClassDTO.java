package com.example.FAMS.dto.requests.ClassRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateClassDTO {

    String classCode;
    String nameClass;
    String totalTimeLearning;
    String classTimeFrom;
    String classTimeTo;
    String status;
    String location;
    List<String> admin;
    String fsu;
    String startDate;
    String endDate;
    String created;
    String review;
    String approve;
    String attendee;
    String attendeePlanned;
    String attendeeAccepted;
    String attendeeActual;
    String trainingProgram;
    List<String> attendeeList;
    List<String> listDay;
    List<TrainerSyllabusDTO> trainer;
}
