package com.tablelog.tablelogback.global.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Season {
    SPRING,
    SUMMER,
    AUTUMN,
    WINTER;
    @JsonCreator
    public static Season fromString(String inputValue) {
        return EnumUtils.fromString(Season.class, inputValue);
    }

}
