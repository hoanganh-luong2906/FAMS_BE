package com.example.FAMS.dto.requests.Calendar;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCalendarRequest {
    private String id;
    private String classid;
    //    private int id;
    private String enrollDate;
    private Time timeFrom;
    private Time timeTo;
    private String value;
}
