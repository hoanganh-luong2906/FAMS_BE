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
public class ClassUserCompositeKey implements Serializable {

    @Column(name = "user_id")
    int userId;

    @Column(name = "class_id")
    String classId;
}
