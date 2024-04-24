package com.example.FAMS.dto.responses.Class;

import com.example.FAMS.dto.requests.ClassRequest.CreateClassDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateClassResponse {

    String message;
    int status;
    CreateClassDTO createdClass;
}
