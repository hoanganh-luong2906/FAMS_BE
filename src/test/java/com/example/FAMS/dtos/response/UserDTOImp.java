package com.example.FAMS.dtos.response;

import com.example.FAMS.dto.UserDTO;
import com.example.FAMS.enums.Role;
import com.example.FAMS.models.UserPermission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTOImp implements UserDTO {
    private int userId;
    private String name;
    private String email;
    private String phone;
    private Date dob;
    private String gender;
    private String createdBy;
    private String modifiedBy;
    private UserPermission role;
    private boolean status;


    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setRole(UserPermission role) {
        this.role = role;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getPhone() {
        return phone;
    }

    @Override
    public Date getDob() {
        return dob;
    }

    @Override
    public String getGender() {
        return gender;
    }

    @Override
    public UserPermission getRole() {
        return role;
    }

    @Override
    public boolean isStatus() {
        return status;
    }

    @Override
    public String getCreatedBy() {
        return createdBy;
    }

    @Override
    public Date getCreatedDate() {
        return getCreatedDate();
    }

    @Override
    public String getModifiedBy() {
        return modifiedBy;
    }

    @Override
    public Date getModifiedDate() {
        return null;
    }
}
