package com.example.FAMS.models.composite_key;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassLocationCompositeKey {

    @Column(name = "class_code")
    String classCode;

    @Column(name = "location_id")
    Long locationId;
}
