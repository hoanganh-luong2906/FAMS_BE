package com.example.FAMS.dto.requests;

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
public class UpdateRequest {
    private Role role;
    private String name;
    private String phone;
    private Date dob;
    private String gender;
    private boolean status;
    private String modifiedBy;
}
