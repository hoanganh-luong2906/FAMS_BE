package com.example.FAMS.services;

import com.example.FAMS.dto.requests.UpdatePasswordRequest;
import com.example.FAMS.dto.requests.UpdateRequest;
import com.example.FAMS.dto.responses.ResponseObject;
import com.example.FAMS.dto.responses.UpdateResponse;
import org.springframework.http.ResponseEntity;

public interface UserService {
    ResponseEntity<ResponseObject> getAll();

    ResponseEntity<ResponseObject> pagination(int pageNo);

    UpdateResponse updateUser(String userEmail, UpdateRequest updateRequest);

    ResponseObject deleteUser(String mail);

    ResponseObject updatePassword(UpdatePasswordRequest updateRequest);

    ResponseEntity<ResponseObject> getAllTrainersByRole();

    ResponseEntity<ResponseObject> getAllTraineeByRole();

    ResponseEntity<ResponseObject> getAllAdminsByRole();

    ResponseEntity<ResponseObject> getAllAdminAndSuperAdminByRole();

    String authorizeAccount(String emailAddress);

    ResponseEntity<ResponseObject> changeStatus(int id, String type);
}
