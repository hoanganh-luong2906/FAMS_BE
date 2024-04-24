package com.example.FAMS.dto.responses.Syllabus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateSyllabusResponse {

    int status;
    String message;
    List<PresignedUrlResponse> url;
}
