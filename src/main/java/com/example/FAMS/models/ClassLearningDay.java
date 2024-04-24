package com.example.FAMS.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Time;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Table(name = "classLearningDay")
public class ClassLearningDay {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private int date;

    private int month;

    private int year;

    @Column(name = "enroll_date", nullable = false)
    private Date enrollDate;

    @Column(name = "time_from", nullable = false)
    private Time timeFrom;

    @Column(name = "time_to", nullable = false)
    private Time timeTo;

    @ManyToOne(cascade = CascadeType.MERGE, optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", referencedColumnName = "class_code")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonBackReference
    private Class classId;

//    @ManyToOne(cascade = CascadeType.MERGE, optional = false, fetch = FetchType.LAZY)
//    @JoinColumn(name = "location_id", referencedColumnName = "location_id")
//    @EqualsAndHashCode.Exclude
//    @ToString.Exclude
//    @JsonBackReference
//    private Location locationId;

}
