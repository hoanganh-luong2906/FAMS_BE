package com.example.FAMS.services;

import com.example.FAMS.dto.responses.ResponseObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface TrainingMaterialService {

    ResponseObject uploadTrainingMaterial(MultipartFile file) throws IOException;

    byte[] downloadTrainingMaterials(String fileName) throws IOException, RuntimeException;

    ResponseEntity<ResponseObject> deleteTrainingMaterial(String fileName);
}
