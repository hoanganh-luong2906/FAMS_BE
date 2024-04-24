package com.example.FAMS.controllers;

import com.example.FAMS.dto.responses.ResponseObject;
import com.example.FAMS.services.TrainingMaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/training-material")
@RequiredArgsConstructor
public class TrainingMaterialController {

    private final TrainingMaterialService trainingMaterialService;

    @PostMapping("/upload")
    public ResponseEntity<ResponseObject> uploadTrainingMaterial(@RequestBody MultipartFile file) {
        ResponseObject response = ResponseObject.builder().build();
        try {
            response = trainingMaterialService.uploadTrainingMaterial(file);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status("Fail")
                    .message(e.getMessage())
                    .build());
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<?> downloadTrainingMaterial(
            @PathVariable(name = "fileName") String fileName
    ) {
        ResponseObject response = ResponseObject.builder().build();
        try {
            byte[] data = trainingMaterialService.downloadTrainingMaterials(fileName);
            ByteArrayResource byteArrayResource = new ByteArrayResource(data);
            return ResponseEntity.ok()
                    .contentLength(data.length)
                    .header("Content-type", "application/octet-stream")
                    .header(
                            "Content-disposition",
                            "attachment; filename=\"" + fileName + "\""
                    )
                    .body(byteArrayResource);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Fail to download file: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<ResponseObject> deleteTrainingMaterial(
            @PathVariable(name = "fileName") String fileName
    ) {
        return trainingMaterialService.deleteTrainingMaterial(fileName);
    }
}
