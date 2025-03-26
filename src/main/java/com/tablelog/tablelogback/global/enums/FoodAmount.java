package com.tablelog.tablelogback.global.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum FoodAmount {
    ts,
    TS;

    @JsonCreator
    public static FoodAmount fromString(String inputValue) {
        return EnumUtils.fromString(FoodAmount.class, inputValue);
    }
}
