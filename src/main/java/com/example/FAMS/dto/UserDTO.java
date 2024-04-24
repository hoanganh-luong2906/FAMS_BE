package com.example.FAMS.dto;

import com.example.FAMS.models.UserPermission;

import java.util.Date;

public interface UserDTO {

    String getName();

    String getEmail();

    String getPhone();

    Date getDob();

    String getGender();

    UserPermission getRole();

    boolean isStatus();

    String getCreatedBy();

    Date getCreatedDate();

    String getModifiedBy();

    Date getModifiedDate();

}
