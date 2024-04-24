package com.example.FAMS.models.composite_key;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class SyllabusTrainingUnitTrainingContentCompositeKey implements Serializable {

    @Column(name = "content_code")
    int contentCode;

    @Embedded
    SyllabusTrainingUnitCompositeKey id;

}
