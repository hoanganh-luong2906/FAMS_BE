package com.example.FAMS.models;


import com.example.FAMS.models.composite_key.SyllabusStandardOutputCompositeKey;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "syllabus_objective")
public class SyllabusObjective {

    @EmbeddedId
    SyllabusStandardOutputCompositeKey id;

    @ManyToOne(cascade = CascadeType.MERGE, optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_code", referencedColumnName = "topic_code")
    @MapsId("topicCode")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonBackReference
    private Syllabus topicCode;

    @ManyToOne(cascade = CascadeType.MERGE, optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "output_code", referencedColumnName = "output_code", columnDefinition = "VARCHAR(10)")
    @MapsId("outputCode")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonBackReference
    private StandardOutput outputCode;

}
