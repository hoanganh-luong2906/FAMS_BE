//package com.example.FAMS.models;
//
//import com.fasterxml.jackson.annotation.JsonManagedReference;
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.util.HashSet;
//import java.util.Set;
//
//@Entity
//@Table
//@AllArgsConstructor
//@NoArgsConstructor
//@Data
//@Builder
//public class Fsu {
//
////    @Id
////    @Column(name = "fsu_id")
////    String fsuId;
////
////    @Column(name = "fsu_name")
////    String fsuName;
////
////    @OneToMany(mappedBy = "fsuId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
////    @JsonManagedReference
////    private Set<Location> location = new HashSet<>();
////
////    @OneToMany(mappedBy = "fsu", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
////    @JsonManagedReference
////    private Set<Class> classes = new HashSet<>();
////
////    @OneToMany(mappedBy = "fsu", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
////    @JsonManagedReference
////    private Set<User> users = new HashSet<>();
//
//
////    @Id
////    @GeneratedValue(strategy = GenerationType.IDENTITY)
////    private int id;
////
////    private String fsuCode;
////
////    @OneToMany(mappedBy = "fsu", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
////    @JsonManagedReference
////    private Set<Class> classes = new HashSet<>();
////
////    @OneToMany(mappedBy = "fsu", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
////    @JsonManagedReference
////    private Set<User> users = new HashSet<>();
//
//
//
//}
