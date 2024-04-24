package com.example.FAMS.models;

import com.example.FAMS.models.composite_key.SyllabusTrainingUnitTrainingContentCompositeKey;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor

//@Table(name = "TrainingContents",
//        indexes = {
//                @Index(name = "idx_content_id", columnList = "content_id")
//        }
//)
public class TrainingContent {

//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    @Column(name = "content_code")
//    private int contentCode;

//    @Column(name = "content_id")
//    private int contentId;

    @EmbeddedId
    SyllabusTrainingUnitTrainingContentCompositeKey id;

    @ManyToOne(cascade = CascadeType.MERGE, optional = false)
    @JoinColumns({
            @JoinColumn(name = "unit_code", referencedColumnName = "unit_code", insertable = false, updatable = false),
            @JoinColumn(name = "topic_code", referencedColumnName = "topic_code", insertable = false, updatable = false)
    })
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    @ToString.Exclude
    @JsonBackReference
    private TrainingUnit unitCode;

//    @OneToMany(mappedBy = "contentCode", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//    @JsonManagedReference
//    private final Set<LearningObjective> learningObjectives = new HashSet<>();

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinColumn(name = "standard_output", referencedColumnName = "output_code")
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    @ToString.Exclude
    @JsonBackReference
    private StandardOutput outputCode;

    @Column(nullable = false, name = "delivery_type")
    private String deliveryType;

    @Column(nullable = false, name = "duration")
    private int duration;

    @Column(nullable = false, name = "training_format")
    private boolean trainingFormat;

    @Column(name = "content_name")
    private String contentName;

    @Column(name = "note")
    private String note;


    @OneToMany(mappedBy = "trainingContent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    @JsonIgnore
    private Set<TrainingMaterial> trainingMaterials;
}
