package com.example.FAMS.dto.responses;

import com.example.FAMS.dto.responses.Syllabus.PresignedUrlResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSyllabusResponse {

    int status;
    String message;
    List<PresignedUrlResponse> url;
//    Syllabus updateSyllabus;


}
