package com.example.FAMS.models.composite_key;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DayDateMonthYearClassCompositeKey {

    @Column(name = "day_id")
    int dayId;

    @Column(name = "date_id")
    int dateId;

    @Column(name = "month_id")
    int monthId;

    @Column(name = "class_id")
    String classId;
}
