package com.example.FAMS.controllers;

import com.example.FAMS.dto.requests.UpdatePermissionRequest;
import com.example.FAMS.dto.responses.GetUserPermissionsResponse;
import com.example.FAMS.dto.responses.ResponseObject;
import com.example.FAMS.enums.Role;
import com.example.FAMS.services.UserPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user-permission")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('USER', 'CLASS_ADMIN', 'SUPER_ADMIN')")
public class UserPermissionController {

    private final UserPermissionService userPermissionService;

    @GetMapping("/get-all")
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<List<GetUserPermissionsResponse>> getAllPermission() {
        List<GetUserPermissionsResponse> list = userPermissionService.getUserPermission();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PutMapping("/grant-permission/{userId}")
    @PreAuthorize("hasAnyAuthority('user:update')")
    public ResponseEntity<ResponseObject> grantPermission(
            @PathVariable int userId, @RequestParam(name = "role", required = true) Role role) {
        ResponseEntity<ResponseObject> grantUser = userPermissionService.grantPermission(userId, role);
        if (grantUser != null) {
            return grantUser;
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // User not found or validation failed
        }
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('user:update')")
    public ResponseEntity<ResponseObject> updatePermission(
            @RequestBody List<UpdatePermissionRequest> updateRequest
    ) {
        return ResponseEntity.ok(userPermissionService.updatePermission(updateRequest));
    }


}
