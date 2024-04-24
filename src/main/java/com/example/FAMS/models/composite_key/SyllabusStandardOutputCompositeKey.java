package com.example.FAMS.models.composite_key;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
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
public class SyllabusStandardOutputCompositeKey implements Serializable {

    @Column(name = "topic_code")
    String topicCode;

    @Column(name = "output_code")
    String outputCode;

}
