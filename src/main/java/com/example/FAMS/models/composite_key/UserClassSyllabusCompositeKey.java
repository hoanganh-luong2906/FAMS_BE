package com.example.FAMS.models.composite_key;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserClassSyllabusCompositeKey {

    @Column(name = "user_id")
    private int userId;

    @Column(name = "topic_code")
    private String topicCode;

    @Column(name = "class_code")
    private String classCode;
}
