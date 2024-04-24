package com.example.FAMS.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClassInfoDTO {
    private String classId;

    private String className;

    private String duration;

    private Time timeFrom;

    private Time timeTo;

    private String status;

    private String fsu;

    private Date startDate;

    private Date endDate;

    private String createdBy;

    private String approve;

    private String review;

    private Date createdDate;

    private String modifiedBy = "";

    private Date modifiedDate = null;

    private boolean deactivated = false;

    private String attendee;

    private int attendeePlanned = 0;

    private int attendeeAccepted = 0;

    private int attendeeActual = 0;

    private String location;
}
