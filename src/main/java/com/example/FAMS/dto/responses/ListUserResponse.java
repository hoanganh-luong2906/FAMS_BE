package com.example.FAMS.dto.responses;

import com.example.FAMS.enums.Role;

import java.util.Date;


public interface ListUserResponse {
    int getUserId();

    String getName();

    String getEmail();

    String getPhone();

    Date getDob();

    String getGender();

    Role getRole();

    boolean isStatus();

    String getCreatedBy();

    String getModifiedBy();
}
