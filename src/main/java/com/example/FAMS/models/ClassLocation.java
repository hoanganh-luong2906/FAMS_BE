//package com.example.FAMS.models;
//
//import com.example.FAMS.models.composite_key.ClassLocationCompositeKey;
//import com.fasterxml.jackson.annotation.JsonBackReference;
//import jakarta.persistence.*;
//import lombok.*;

//@Data
//@Builder
//@AllArgsConstructor
//@NoArgsConstructor
//@Entity
//@Table
//public class ClassLocation {

//    @EmbeddedId
//    ClassLocationCompositeKey id;
//
//    @ManyToOne(cascade = CascadeType.MERGE, optional = false, fetch = FetchType.LAZY)
//    @MapsId("classId")
//    @JoinColumn(name = "class_code", referencedColumnName = "class_code")
//    @EqualsAndHashCode.Exclude
//    @ToString.Exclude
//    @JsonBackReference
//    private Class classCode;
//
//
//    @ManyToOne(cascade = CascadeType.MERGE, optional = false, fetch = FetchType.LAZY)
//    @MapsId("locationId")
//    @JoinColumn(name = "location_id")
//    @EqualsAndHashCode.Exclude
//    @ToString.Exclude
//    @JsonBackReference
//    private Location locationCode;
//
//    private boolean deleted = false;

//}
