package com.example.FAMS.controllers;

import com.example.FAMS.dto.responses.*;
import com.example.FAMS.services.TrainingProgramService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/training-program")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN')")
public class TrainingProgramController {

    private final TrainingProgramService trainingProgram;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('user:create')")
    public ResponseEntity<ResponseObject> createTrainingProgram(
            @RequestBody TrainingProgramDTO trainingProgramDTO) {
        return trainingProgram.createTrainingProgram(trainingProgramDTO);
    }

    @GetMapping("/get/{id}")
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<ResponseObjectVersion2> getTrainingProgram(@PathVariable int id) {
        return trainingProgram.getTrainingProgramByCode(id);
    }

//    @GetMapping("/get-all")
//    @PreAuthorize("hasAuthority('user:read')")
//    public ResponseEntity<ResponseObject> getAllTrainingProgram() {
//        return trainingProgram.getAll();
//    }

    @GetMapping("/get-all")
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<ResponseObject> getAllActiveTrainingProgram() {
        return trainingProgram.getAllActive();
    }

    @GetMapping("/get-all/All")
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<ResponseObject> getAllTrainingProgram() {
        return trainingProgram.getAll();
    }

    @PutMapping("/training-program/{trainingProgramCode}")
    @PreAuthorize("hasAnyAuthority('training:update')")
    public ResponseEntity<ResponseTrainingProgram> updateTrainingProgram(
            @PathVariable int trainingProgramCode,
            @RequestParam String choice,
            @RequestBody TrainingProgramDTO2 trainingProgramDTO) {
//        return trainingProgram.updateTrainingProgram(trainingProgramCode, trainingProgramDTO);
        return trainingProgram.updateTrainingProgram(trainingProgramCode, choice, trainingProgramDTO);

    }


//    @GetMapping("/duplicate/name/{name}")
//    @PreAuthorize("hasAnyAuthority('training:read')")
//    public ResponseEntity<TrainingProgram> duplicateTrainingProgramName(@PathVariable String name) {
//        try {
//            TrainingProgram duplicatedProgram = trainingProgram.duplicateTrainingProgramName(name);
//            return ResponseEntity.ok(duplicatedProgram);
//        } catch (EntityNotFoundException e) {
//            return ResponseEntity.notFound().build();
//        }
//    }


    @PostMapping("/importCSV")
    @PreAuthorize("hasAuthority('training:import')")
    public ResponseEntity<?> loadDataInFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("choice") String choice,
            @RequestParam("separator") String separator,
            @RequestParam("scan") String scan,
            Authentication authentication)
            throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File empty!");
        }
        try {
            return trainingProgram.processDataFromCSV(file, choice, separator, scan, authentication);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject("Failed", "couldn't found the list", e.getMessage()));
        }
    }

    @PostMapping("/search")
    @PreAuthorize("hasAnyAuthority('training:read')")
    public Object searchTrainingProgram(@RequestParam(name = "keyword") String keyword) {
        return trainingProgram.searchTrainingProgram(keyword) != null ?
                trainingProgram.searchTrainingProgram(keyword) : "no data";
    }

    @GetMapping("/activate/{trainingProgramCode}")
    @PreAuthorize("hasAnyAuthority('training:update')")
    public ResponseEntity<ResponseObject> activateTrainingProgram(
            @PathVariable int trainingProgramCode) {
        return trainingProgram.changeTrainingProgramStatus(trainingProgramCode, "Activate");
    }

    @GetMapping("/deactivate/{trainingProgramCode}")
    @PreAuthorize("hasAnyAuthority('training:update')")
    public ResponseEntity<ResponseObject> deactivateTrainingProgram(
            @PathVariable int trainingProgramCode) {
        return trainingProgram.changeTrainingProgramStatus(trainingProgramCode, "De-activate");
    }

    @GetMapping("/TemplateCSV")
    public ResponseEntity<InputStreamResource> downloadTemplateCSV() {
        String csvData = "name,startDate,duration,userID,status,trainingProgramCode";
        String computerAccountName = System.getProperty("user.name");
        try {
            File csvFile = new File("C:/Users/" + computerAccountName + "/Downloads/Template.csv");
            FileOutputStream outputStream = new FileOutputStream(csvFile);
            outputStream.write(csvData.getBytes());
            outputStream.close();
            HttpHeaders headers = new HttpHeaders();

            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=TemplateCSV.csv");
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(new ByteArrayInputStream(csvData.getBytes(StandardCharsets.UTF_8))));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}
