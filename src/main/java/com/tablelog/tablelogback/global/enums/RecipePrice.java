package com.tablelog.tablelogback.global.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum RecipePrice {
    p5000,
    p10000,
    p50000,
    p100000,
    p100000_plus;

    @JsonCreator
    public static RecipePrice fromString(String inputValue) {
        return EnumUtils.fromString(RecipePrice.class, inputValue);
    }
}
