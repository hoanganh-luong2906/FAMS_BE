package com.example.FAMS.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "StandardOutput")
public class StandardOutput {

    @Id
    @Column(name = "output_code", length = 10)
    String outputCode;

    @Column(name = "output_name", length = 300)
    String outputName;

    @Column(name = "output_description", length = 15000)
    String description;

//    @OneToMany(mappedBy = "outputCode", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//    @JsonManagedReference
//    private final Set<LearningObjective> learningObjective = new HashSet<>();

//    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "standardOutputs")
//    @JsonManagedReference
//    private Set<Syllabus> syllabus;

    @OneToMany(mappedBy = "outputCode", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private final Set<SyllabusObjective> syllabusObjectives = new HashSet<>();

    @OneToMany(mappedBy = "outputCode", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private final Set<TrainingContent> trainingContents = new HashSet<>();


}
