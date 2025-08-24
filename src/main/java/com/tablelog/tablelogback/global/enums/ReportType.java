package com.tablelog.tablelogback.global.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ReportType {
    R_BOARD,
    R_RECIPE,
    R_USER,
    R_BOARD_COMMENT,
    R_RECIPE_REVIEW;


    @JsonCreator
    public static ReportType fromString(String inputValue) {
        return EnumUtils.fromString(ReportType.class, inputValue);
    }
}
