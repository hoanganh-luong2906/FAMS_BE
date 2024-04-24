package com.example.FAMS.dto.requests.ClassRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateClass3Request {
    private boolean deleted;
    private String topicCode;
    private int trainingProgramCode;
}
