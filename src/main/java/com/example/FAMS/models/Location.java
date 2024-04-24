//package com.example.FAMS.models;
//
//import com.fasterxml.jackson.annotation.JsonBackReference;
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import com.fasterxml.jackson.annotation.JsonManagedReference;
//import jakarta.persistence.*;
//import lombok.*;
//
//import java.util.HashSet;
//import java.util.Set;
//
//@Data
//@Builder
//@Entity
//@Table
//@AllArgsConstructor
//@NoArgsConstructor
//public class Location {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "location_id")
//    Long locationId;
//
//    @Column(name = "location")
//    String location;
//
////    boolean deleted = false;
//
////    @ManyToOne(cascade = CascadeType.MERGE, optional = false, fetch = FetchType.LAZY)
////    @JoinColumn(name = "fsu_id", referencedColumnName = "fsu_id")
////    @EqualsAndHashCode.Exclude
////    @ToString.Exclude
////    @JsonBackReference
////    private Fsu fsuId;
//
////    @OneToMany(mappedBy = "locationCode", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
////    @JsonManagedReference
////    private Set<ClassLocation> classLocations = new HashSet<>();
//
//    @ManyToOne(cascade = CascadeType.MERGE, optional = false, fetch = FetchType.LAZY)
//    @JoinColumn(name = "class_code", referencedColumnName = "class_code")
//    @EqualsAndHashCode.Exclude
//    @ToString.Exclude
//    @JsonBackReference
//    private Class classId;
//
//    @OneToMany(mappedBy = "locationId", cascade = CascadeType.ALL)
//    @JsonManagedReference
//    @JsonIgnore
//    private Set<ClassLearningDay> classLearningDays = new HashSet<>();
//
//}
