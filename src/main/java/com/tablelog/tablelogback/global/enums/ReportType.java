package com.tablelog.tablelogback.global.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ReportType {
    R_BOARD,
    R_RECIPE,
    R_USER;


    @JsonCreator
    public static ReportType fromString(String inputValue) {
        return EnumUtils.fromString(ReportType.class, inputValue);
    }
}
