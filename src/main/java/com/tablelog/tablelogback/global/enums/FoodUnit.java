package com.tablelog.tablelogback.global.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum FoodUnit {
    kg,
    g,
    ml,
    L,
    cup,
    tbsp;

    @JsonCreator
    public static FoodUnit fromString(String inputValue) {
        return EnumUtils.fromString(FoodUnit.class, inputValue);
    }
}
