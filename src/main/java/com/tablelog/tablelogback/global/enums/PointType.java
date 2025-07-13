package com.tablelog.tablelogback.global.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum PointType {
    EARN, // 적립
    USE; // 사용

    @JsonCreator
    public static PointType fromString(String inputValue) {
        return EnumUtils.fromString(PointType.class, inputValue);
    }
}
