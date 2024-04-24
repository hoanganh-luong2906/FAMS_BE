package com.example.FAMS.models.composite_key;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SyllabusTrainingUnitCompositeKey implements Serializable {

    @Column(name = "unit_code")
    int uCode;

    @Column(name = "topic_code")
    String tCode;
}
