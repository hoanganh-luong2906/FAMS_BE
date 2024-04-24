package com.example.FAMS.dto.responses.Syllabus;


import com.example.FAMS.dto.responses.Class.UserDTO;
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
public class SyllabusResponse {

    int responseStatus = 0;
    String responseMessage = "";
    String syllabusName = "";
    String syllabusCode = "";
    String createdOn = "";
    UserDTO createdBy = null;
    String duration = "";
    String syllabusStatus = "";
    String level = "";
    String attendeeNumber = "";
    String technicalRequirement = "";
    String courseObjective = "";
    String trainingPrinciple = "";
    String modifiedOn = "";
    UserDTO modifiedBy = null;
    String version = "";
    String assignmentLab = "0%";
    String conceptLecture = "0%";
    String guideReview = "0%";
    String testQuiz = "0%";
    String exam = "0%";
    String quiz = "0%";
    String assignment = "0%";
    String final_ = "0%";
    String finalTheory = "0%";
    String finalPractice = "0%";
    String gpa = "0%";
    boolean deleted = false;
    List<String> syllabusObjectiveList = new ArrayList<>();
    List<DayDTO> dayList = new ArrayList<>();
}
