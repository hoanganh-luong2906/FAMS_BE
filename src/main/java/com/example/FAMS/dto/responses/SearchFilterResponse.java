package com.example.FAMS.dto.responses;

import java.sql.Time;

public interface SearchFilterResponse {
    String getLocation();

    Time getTimeFrom();

    Time getTimeTo();

    String getName();

    String getStatus();

    String getFsu();

}
