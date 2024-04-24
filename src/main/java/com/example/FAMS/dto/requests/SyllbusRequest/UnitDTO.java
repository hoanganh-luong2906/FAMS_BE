package com.example.FAMS.dto.requests.SyllbusRequest;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnitDTO {

    String unitName = "";
    int unitCode;
    List<ContentDTO> contents;
}
