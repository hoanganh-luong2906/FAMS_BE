package com.example.FAMS.dto.responses;

import com.example.FAMS.models.Syllabus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CsvResponse {
    private String status;
    private Syllabus csvSyllabus;
}
