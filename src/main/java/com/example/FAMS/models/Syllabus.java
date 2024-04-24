package com.example.FAMS.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Table(name = "Syllabus")
public class Syllabus {

    @Id
    @Column(name = "topic_code", nullable = false)
    private String topicCode;

    @Column(name = "topic_name", nullable = false)
    private String topicName;

    @Column(name = "number_of_day")
    private int duration = 1;

    @Column(name = "technical_group", nullable = false, length = 2048)
    private String technicalGroup;

    @Column(nullable = false)
    private String version;

    @Column(name = "training_audience", nullable = false)
    private int trainingAudience;

    @Column(name = "topic_outline")
    private String topicOutline;

    @Lob
    @Column(name = "training_principles")
    private String trainingPrinciples;

    @Column(nullable = false)
    private String priority;

    @Column(name = "publish_status", nullable = false)
    private String publishStatus;

    @ManyToOne(cascade = CascadeType.MERGE, optional = false, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "user_syllabus", referencedColumnName = "user_id")
    @JsonIgnore
    @JsonBackReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User createdBy;

    @Column(name = "created_date", nullable = false)
    private Date createdDate;

    @Column(name = "modified_by")
    private String modifiedBy;

    @Column(name = "modified_date")
    private Date modifiedDate;

    @Column(name = "course_objective", length = 5000, nullable = false)
    private String courseObjective;

    private boolean deleted = false;

    private String assignmentLab;

    private String conceptLecture;

    private String guideReview;

    private String testQuiz;

    private String exam;

    private String quiz;

    private String assignment;

    @Column(name = "final")
    private String final_;

    private String finalTheory;

    private String finalPractice;

    private String gpa;

    @OneToMany(mappedBy = "topicCode", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    @JsonIgnore
    private final Set<TrainingProgramSyllabus> tps = new HashSet<>();

    @OneToMany(mappedBy = "syllabus", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
//    @JsonIgnore
    private final Set<TrainingUnit> tu = new HashSet<>();

//    @ManyToOne(cascade = CascadeType.MERGE, optional = false, fetch = FetchType.LAZY)
//    @JoinColumn(nullable = false, name = "user_syllabus", referencedColumnName = "user_id")
//    @JsonIgnore
//    @JsonBackReference
//    @ToString.Exclude
//    @EqualsAndHashCode.Exclude
//    private User userID;

    @OneToMany(mappedBy = "topicCode", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private final Set<UserClassSyllabus> userSyllabus = new HashSet<>();

    @OneToMany(mappedBy = "topicCode", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private final Set<SyllabusObjective> syllabusObjectives = new HashSet<>();

//    @OneToMany(mappedBy = "syllabus", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @Column(name = "training_materials")
//    @JsonManagedReference
//    @JsonIgnore
//    private Set<TrainingMaterial> trainingMaterials;


}
