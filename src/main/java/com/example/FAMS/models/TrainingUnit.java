package com.example.FAMS.models;

import com.example.FAMS.models.composite_key.SyllabusTrainingUnitCompositeKey;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TrainingUnit")
//@IdClass(SyllabusTrainingUnitCompositeKey.class)
public class TrainingUnit {

    @EmbeddedId
    SyllabusTrainingUnitCompositeKey id;

//    @Id
//    @Column(name = "unit_code", nullable = false)
//    String unitCode;
//
//    @Id
//    @Column(name = "topic_code", nullable = false)
//    String topicCode;


    @ManyToOne(cascade = CascadeType.MERGE, optional = false, fetch = FetchType.LAZY)
    @MapsId("topicCode")
    @JoinColumn(name = "topic_code", referencedColumnName = "topic_code")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonBackReference
    private Syllabus syllabus;

    @OneToMany(mappedBy = "unitCode", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private final Set<TrainingContent> trainingContents = new HashSet<>();


    @Column(name = "unit_name", nullable = false)
    private String unitName;

    @Column(name = "day_number", nullable = false)
    private int dayNumber;


}
