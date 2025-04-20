package com.tablelog.tablelogback.global.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum UserProvider {
    local,
    kakao,
    google;

    @JsonCreator
    public static FoodAmount fromString(String inputValue) {
        return EnumUtils.fromString(FoodAmount.class, inputValue);
    }
}
