package com.example.FAMS.services;

import com.example.FAMS.dto.responses.*;
import com.example.FAMS.models.TrainingProgram;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

public interface TrainingProgramService {
    ResponseEntity<ResponseObject> createTrainingProgram(TrainingProgramDTO trainingProgramDTO);

    ResponseEntity<ResponseObject> getAllActive();

    ResponseEntity<ResponseObject> getAll();

//    ResponseEntity<ResponseTrainingProgram> updateTrainingProgram(int trainingProgramCode,String choice, TrainingProgramDTO2 trainingProgramDTO);

    ResponseEntity<ResponseTrainingProgram> updateTrainingProgram(int trainingProgramCode, String choice, TrainingProgramDTO2 trainingProgramDTO);

    ResponseEntity<ResponseObject> changeTrainingProgramStatus(int trainingProgramCode, String value);

    ResponseEntity<ResponseObject> processDataFromCSV(MultipartFile file, String choice, String separator, String scan, Authentication authentication) throws Exception;

    TrainingProgram searchTrainingProgram(String keyword);

    ResponseEntity<ResponseObjectVersion2> getTrainingProgramByCode(int code);
}

