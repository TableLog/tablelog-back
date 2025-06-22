package com.tablelog.tablelogback.global.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum RecipeCalorieRange {
    UNDER_500( null, 500),
    OVER_1000(1000, null),
    RANGE_500_1000(500, 1000);

    private final Integer min;
    private final Integer max;

    RecipeCalorieRange(Integer min, Integer max) {
        this.min = min;
        this.max = max;
    }

    public Integer getMin() {
        return min;
    }

    public Integer getMax() {
        return max;
    }

    @JsonCreator
    public static RecipeCalorieRange fromString(String inputValue) {
        return EnumUtils.fromString(RecipeCalorieRange.class, inputValue);
    }
}
