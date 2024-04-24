package com.example.FAMS.models.composite_key;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@Builder
public class TrainingContentLearningObjectiveCompositeKey implements Serializable {

    @Column(name = "content_code")
    int trainingContentCode;

    @Column(name = "output_code", length = 10)
    String learningObjectiveCode;

    @Column(name = "topic_code")
    String topicCode;

}
