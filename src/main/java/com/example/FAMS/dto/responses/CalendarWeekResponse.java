package com.example.FAMS.dto.responses;

import java.sql.Time;
import java.util.Date;

public interface CalendarWeekResponse {
    Time getTimeFrom();

    Time getTimeTo();

    String getClassCode();

    String getStatus();

    Date getEnrollDate();

    Date getStartDate();

    Date getEndDate();
}
