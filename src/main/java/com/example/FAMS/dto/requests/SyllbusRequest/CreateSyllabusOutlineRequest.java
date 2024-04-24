package com.example.FAMS.dto.requests.SyllbusRequest;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateSyllabusOutlineRequest {
    String topicCode;
    String topicName;
    String version;
    String technicalRequirement;
    String priority;
    String courseObjective;
    String publishStatus = "inactive";
    String trainingPrinciple;
    String creatorEmail;
    String trainingAudience;
    int assignmentLab = 0;
    int conceptLecture = 0;
    int guideReview = 0;
    int testQuiz = 0;
    int exam = 0;
    int quiz = 0;
    int assignment = 0;
    int fin = 0;
    int finalTheory = 0;
    int finalPractice = 0;
    int gpa = 0;
    List<DayDTO> syllabus;
//    List<TrainingMaterialDTO> trainingMaterials;

}
