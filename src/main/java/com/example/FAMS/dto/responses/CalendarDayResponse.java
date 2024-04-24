package com.example.FAMS.dto.responses;

import java.sql.Time;
import java.util.Date;

public interface CalendarDayResponse {
    Time getTimeFrom();

    Time getTimeTo();

    String getClassCode();

    String getStatus();

    String getUserType();

    Integer getNumberOfDay();

    String getName();

    Date getStartDate();

    Date getEndDate();
}
