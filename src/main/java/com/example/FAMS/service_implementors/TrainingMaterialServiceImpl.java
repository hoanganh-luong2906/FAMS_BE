package com.example.FAMS.service_implementors;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.example.FAMS.dto.responses.ResponseObject;
import com.example.FAMS.models.TrainingMaterial;
import com.example.FAMS.repositories.TrainingMaterialDAO;
import com.example.FAMS.services.TrainingMaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TrainingMaterialServiceImpl implements TrainingMaterialService {

    private final AmazonS3 s3Client;
    private final TrainingMaterialDAO trainingMaterialDAO;

    @Value("${application.bucket.name}")
    private String bucketName;

    @Override
    public ResponseObject uploadTrainingMaterial(MultipartFile file) throws IOException {
        File fileObj = convertMultipartFileToFile(file);
        var result = s3Client.putObject(new PutObjectRequest(bucketName, fileObj.getName(), fileObj));
        TrainingMaterial trainingMaterial = TrainingMaterial.builder()
                .material(fileObj.getName())
                .source("https://fams-datas.s3.ap-southeast-1.amazonaws.com/" + fileObj.getName())
                .build();
        var savedTrainingMaterial = trainingMaterialDAO.save(trainingMaterial);
        fileObj.delete();
        return ResponseObject.builder()
                .status("Successful")
                .message("File " + fileObj.getName() + " uploaded")
                .payload(savedTrainingMaterial)
                .build();
    }

    @Override
    public byte[] downloadTrainingMaterials(String fileName) throws IOException, RuntimeException {
        if (isFileExisted(fileName)) {
            S3Object s3Object = s3Client.getObject(bucketName, fileName);
            S3ObjectInputStream inputStream = s3Object.getObjectContent();
            return IOUtils.toByteArray(inputStream);
        } else {
            throw new RuntimeException("File not found");
        }

    }

    @Override
    public ResponseEntity<ResponseObject> deleteTrainingMaterial(String fileName) {
        if (isFileExisted(fileName)) {
            deleteFileInDatabase(fileName);
            s3Client.deleteObject(bucketName, fileName);
            return ResponseEntity
                    .ok()
                    .body(ResponseObject.builder()
                            .status("Success")
                            .message("File " + fileName + " removed.")
                            .build());
        }
        return ResponseEntity
                .badRequest()
                .body(ResponseObject.builder()
                        .status("Fail")
                        .message("File " + fileName + " is not found.")
                        .build());
    }

    private boolean isFileExisted(String fileName) {
        List<TrainingMaterial> list = trainingMaterialDAO.findAll();
        for (TrainingMaterial t : list) {
            if (t.getMaterial().equalsIgnoreCase(fileName))
                return true;
        }
        return false;
    }

    private void deleteFileInDatabase(String fileName) {
        List<TrainingMaterial> list = trainingMaterialDAO.findAll();
        for (TrainingMaterial t : list) {
            if (t.getMaterial().equalsIgnoreCase(fileName))
                list.remove(t);
            break;
        }
        trainingMaterialDAO.saveAll(list);
    }

    private File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
        File convertedFile = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        FileOutputStream fos = new FileOutputStream(convertedFile);
        fos.write(multipartFile.getBytes());
        return convertedFile;
    }
}
