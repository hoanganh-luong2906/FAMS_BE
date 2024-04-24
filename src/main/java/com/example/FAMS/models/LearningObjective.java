//package com.example.FAMS.models;
//
//import com.example.FAMS.models.composite_key.TrainingContentLearningObjectiveCompositeKey;
//import com.fasterxml.jackson.annotation.JsonBackReference;
//import jakarta.persistence.*;
//import lombok.*;
//
//@Entity
//@Data
//@AllArgsConstructor
//@Builder
//@NoArgsConstructor
//public class LearningObjective {
//
////    @EmbeddedId
////    TrainingContentLearningObjectiveCompositeKey id;
//
//    @Id
//    @Column(name = "learning_objective_code")
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private int learningObjectiveCode;
//
//    @Column(nullable = false)
//    private String name;
//
//    @Column(nullable = false)
//    private String type;
//
//    @Column(nullable = false)
//    private String description;
//
////    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "learningObjectives")
////    @JsonManagedReference
////    private Set<Syllabus> syllabus;
//
////    @ManyToOne(cascade = CascadeType.MERGE, optional = false, fetch = FetchType.EAGER)
////    @JoinColumn(name = "topic_code", referencedColumnName = "topic_code")
////    @MapsId("topicCode")
////    @EqualsAndHashCode.Exclude
////    @ToString.Exclude
//////    @JsonIgnore
////    @JsonBackReference
////    private Syllabus topicCode;
//
//    @ManyToOne(cascade = CascadeType.MERGE, optional = false, fetch = FetchType.EAGER)
//    @JoinColumn(name = "content_code", referencedColumnName = "content_code")
////    @MapsId("contentCode")
//    @EqualsAndHashCode.Exclude
//    @ToString.Exclude
////    @JsonIgnore
//    @JsonBackReference
//    private TrainingContent contentCode;
//
//    @ManyToOne(cascade = CascadeType.MERGE, optional = false, fetch = FetchType.EAGER)
//    @JoinColumn(name = "output_code", referencedColumnName = "output_code")
////    @MapsId("outputCode")
//    @EqualsAndHashCode.Exclude
//    @ToString.Exclude
//    @JsonBackReference
//    private StandardOutput outputCode;
//
//}
