package com.example.FAMS.dto.requests.ClassRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateClassDTO {

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
    String attendee;
    String attendeePlanned;
    String attendeeAccepted;
    String attendeeActual;
    String moderEmail;
    String trainingProgram;
    List<String> attendeeList;
    List<String> listDay;
    List<TrainerSyllabusDTO> trainer;
}
