package com.example.FAMS.dto.requests.SyllbusRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileDTO {

    int unitCode;
    int contentCode;
    List<String> fileName;
}
