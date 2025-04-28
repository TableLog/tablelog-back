package com.tablelog.tablelogback.global.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum CookingTime {
    minute_10,
    minute_30,
    hour_1,
    hour_1_plus;

    @JsonCreator
    public static CookingTime fromString(String inputValue) {
        return EnumUtils.fromString(CookingTime.class, inputValue);
    }
}
