package com.example.FAMS.dto.responses;

import com.example.FAMS.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserWithRoleDTO {
    private String name;
    private Role role;
    private String email;
    private String phone;
    private Date dob;
    private String gender;
    private boolean status;
    private String createdBy;
    private Date createdDate;
    private String modifiedBy;
    private Date modifiedDate;
}
