package com.example.FAMS.dto.responses.Class;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClassDetailResponse {

    //    TrainingProgramDTO trainingProgram;
    String trainingProgram;
    List<SyllabusDTO> syllabusList = new ArrayList<>();
    List<String> listDay = new ArrayList<>();
    private String message = "";
    private String oldClassCode;
    private String classCode;
    private String nameClass;
    private String totalTimeLearning;
    private String status;
    private String classTimeFrom;
    private String classTimeTo;
    private String location;
    private String fsu;
    private String startDate;
    private String endDate;
    private UserDTO created;
    private UserDTO review;
    private UserDTO approve;
    private String attendee;
    private String attendeePlanned;
    private String attendeeAccepted;
    private String attendeeActual;
    private String createdDate;
    private UserDTO modifiedBy = null;
    private String modifiedDate = null;
    private boolean deactivated = false;
    private List<TrainerDTO> trainer = new ArrayList<>();
    private List<UserDTO> admin = new ArrayList<>();
    private List<UserDTO> attendeeList = new ArrayList<>();

    public ClassDetailResponse(String message) {
        this.message = message;

    }
}
