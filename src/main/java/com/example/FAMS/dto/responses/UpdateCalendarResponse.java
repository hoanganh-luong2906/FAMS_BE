package com.example.FAMS.dto.responses;

import com.example.FAMS.models.ClassLearningDay;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCalendarResponse {
    private String status;
    private ClassLearningDay updateClassLearningDay;
}
