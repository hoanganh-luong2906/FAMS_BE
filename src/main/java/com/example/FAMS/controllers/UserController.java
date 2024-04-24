package com.example.FAMS.controllers;

import com.example.FAMS.dto.requests.UpdatePasswordRequest;
import com.example.FAMS.dto.requests.UpdateRequest;
import com.example.FAMS.dto.responses.ResponseObject;
import com.example.FAMS.dto.responses.UpdateResponse;
import com.example.FAMS.services.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/get-all")
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<ResponseObject> getAllUser() {
        return userService.getAll();
    }

    @GetMapping("/get-all/trainee")
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<ResponseObject> getAllTrainee() {
        return userService.getAllTraineeByRole();
    }

    @GetMapping("/get-all/trainer")
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<ResponseObject> getAllTrainer() {
        return userService.getAllTrainersByRole();
    }

    @GetMapping("/get-all/admin")
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<ResponseObject> getAllAdmin() {
        return userService.getAllAdminsByRole();
    }

    @GetMapping("/get-all/admin-and-superadmin")
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<ResponseObject> getAllAdminAndSuperAdmin() {
        return userService.getAllAdminAndSuperAdminByRole();
    }

    @GetMapping("/pagination")
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<ResponseObject> getPagination(
            @RequestParam(defaultValue = "0", name = "pageNo") Integer pageNo
    ) {
        return userService.pagination(pageNo);
    }

    @PutMapping("/update-user/{userEmail}")
    @PreAuthorize("hasAuthority('user:update')")
    public ResponseEntity<UpdateResponse> updateUser(
            @PathVariable String userEmail,
            @RequestBody UpdateRequest updateRequest
    ) {
        UpdateResponse response = new UpdateResponse();
        try {
            response = userService.updateUser(userEmail, updateRequest);
        } catch (Exception e) {
            response = UpdateResponse.builder()
                    .status("ERROR: " + e.getMessage())
                    .updatedUser(null)
                    .build();
        }
        return ResponseEntity.ok(response);
    }

    //    @DeleteMapping("/delete/{email}")
    //    @PreAuthorize("hasAnyAuthority('user:delete')")
    //    public ResponseEntity<ResponseObject> deletePermission(@PathVariable String email) {
    //        return ResponseEntity.ok(userService.deleteUser(email));
    //    }

    @GetMapping("/authorize/{emailAddress}")
    public ResponseEntity<String> authorizeAccount(
            @NonNull @PathVariable String emailAddress
    ) {
        try {
            String response = userService.authorizeAccount(emailAddress);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }

    }

    @PutMapping("/update-password")
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<ResponseObject> updatePassword(@RequestBody UpdatePasswordRequest updateRequest) {
        try {
            ResponseObject response = userService.updatePassword(updateRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(e.getMessage())
                    .build());
        }
    }

    @GetMapping("/active/{id}")
    @PreAuthorize("hasAuthority('user:update')")
    public ResponseEntity<ResponseObject> activeUser(@PathVariable int id) {
        return userService.changeStatus(id, "active");
    }

    @GetMapping("/inactive/{id}")
    @PreAuthorize("hasAuthority('user:update')")
    public ResponseEntity<ResponseObject> inactiveUser(@PathVariable int id) {
        return userService.changeStatus(id, "inactive");
    }
}
