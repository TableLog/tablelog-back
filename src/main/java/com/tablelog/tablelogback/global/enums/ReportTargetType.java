package com.tablelog.tablelogback.global.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ReportTargetType {
    R_BOARD,
    R_RECIPE,
    R_USER;


    @JsonCreator
    public static ReportTargetType fromString(String inputValue) {
        return EnumUtils.fromString(ReportTargetType.class, inputValue);
    }
}
