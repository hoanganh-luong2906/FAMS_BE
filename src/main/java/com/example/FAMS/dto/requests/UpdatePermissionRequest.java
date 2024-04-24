package com.example.FAMS.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePermissionRequest {

    private String roleName;
    private String syllabus;
    private String training;
    private String userclass;
    private String learningMaterial;
    private String userManagement;

}
