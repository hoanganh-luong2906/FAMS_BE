package com.example.FAMS.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {

    USER_CREATE("user:create"),
    USER_VIEW("user:read"),
    USER_MODIFY("user:update"),
    USER_DELETE("user:delete"),
    USER_IMPORT("user:import"),

    SYLLABUS_CREATE("syllabus:create"),
    SYLLABUS_VIEW("syllabus:read"),
    SYLLABUS_MODIFY("syllabus:update"),
    SYLLABUS_DELETE("syllabus:delete"),
    SYLLABUS_IMPORT("syllabus:import"),

    TRAINING_CREATE("training:create"),
    TRAINING_VIEW("training:read"),
    TRAINING_MODIFY("training:update"),
    TRAINING_DELETE("training:delete"),
    TRAINING_IMPORT("training:import"),

    CLASS_CREATE("class:create"),
    CLASS_VIEW("class:read"),
    CLASS_MODIFY("class:update"),
    CLASS_DELETE("class:delete"),
    CLASS_IMPORT("class:import"),

    MATERIAL_CREATE("material:create"),
    MATERIAL_VIEW("material:read"),
    MATERIAL_MODIFY("material:update"),
    MATERIAL_DELETE("material:delete"),
    MATERIAL_IMPORT("material:import");

    @Getter
    private final String permission;

}
