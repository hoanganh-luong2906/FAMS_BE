package com.example.FAMS.services;

import com.example.FAMS.dto.requests.UpdatePermissionRequest;
import com.example.FAMS.dto.responses.GetUserPermissionsResponse;
import com.example.FAMS.dto.responses.ResponseObject;
import com.example.FAMS.enums.Role;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserPermissionService {

    ResponseEntity<ResponseObject> grantPermission(int userID, Role role);

    List<GetUserPermissionsResponse> getUserPermission();

    ResponseObject updatePermission(List<UpdatePermissionRequest> updateRequest);
}
