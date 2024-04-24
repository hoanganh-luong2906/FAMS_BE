package com.example.FAMS.dtos.response;

import com.example.FAMS.dto.responses.ListUserResponse;
import com.example.FAMS.enums.Role;

import java.util.Date;

public class ListUserResponseImpl implements ListUserResponse {
    private int userId;
    private String name;
    private String email;
    private String phone;
    private Date dob;
    private String gender;
    private String createdBy;
    private String modifiedBy;
    private Role role;
    private boolean status;


    public ListUserResponseImpl(int userId, String name, String email, String phone, Date dob, String gender, Role role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.dob = dob;
        this.gender = gender;
        this.role = role;
    }

    @Override
    public int getUserId() {
        return userId;
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
    public Role getRole() {
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
    public String getModifiedBy() {
        return modifiedBy;
    }
}
