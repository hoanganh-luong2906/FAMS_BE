package com.example.FAMS.models;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Time;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Table(name = "Class")
public class Class {
    @Id
    @Column(name = "class_code", nullable = false)
    private String classId;

    @ManyToOne(cascade = CascadeType.MERGE, optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "training_program_code")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonBackReference
    private TrainingProgram trainingProgram;

    @Column(name = "class_name", nullable = false)
    private String className;

    @Column(name = "duration", nullable = false)
    private String duration;

    @Column(name = "time_from")
    private Time timeFrom;

    @Column(name = "time_to")
    private Time timeTo;

    @Column(name = "status", nullable = false)
    private String status;

    private String fsu;

    @Column(name = "start_date", nullable = false)
    @DateTimeFormat(pattern = "MM/dd/yyyy")
    private Date startDate;

    @Column(name = "end_date", nullable = false)
    @DateTimeFormat(pattern = "MM/dd/yyyy")
    private Date endDate;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    private String approve;

    private String review;

    @Column(name = "created_date", nullable = false)
    @DateTimeFormat(pattern = "MM/dd/yyyy")
    private Date createdDate;

    @Column(name = "modified_by")
    private String modifiedBy = "";

    @Column(name = "modified_date")
    @DateTimeFormat(pattern = "MM/dd/yyyy")
    private Date modifiedDate = null;

    private boolean deactivated = false;

    @Column(name = "attendee")
    private String attendee;

    @Column(name = "attendee_planned")
    private int attendeePlanned = 0;

    @Column(name = "attendee_accepted")
    private int attendeeAccepted = 0;

    @Column(name = "attendee_actual")
    private int attendeeActual = 0;

    @OneToMany(mappedBy = "classId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    @JsonIgnore
    private Set<ClassUser> classUsers = new HashSet<>();

    @OneToMany(mappedBy = "classCode", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private Set<UserClassSyllabus> userClassSyllabus = new HashSet<>();

    @OneToMany(mappedBy = "classId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private Set<ClassLearningDay> classLearningDays = new HashSet<>();

//    @OneToMany(mappedBy = "classCode", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @JsonManagedReference
//    private Set<ClassLocation> classLocations = new HashSet<>();

//    @OneToMany(mappedBy = "classId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @JsonManagedReference
//    private Set<Location> locations = new HashSet<>();

    private String location;


}
