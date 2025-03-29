package com.tablelog.tablelogback.global.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum RecipeCategory {
    한식,
    양식,
    중식,
    일식,
    기타,
    디저트;

    @JsonCreator
    public static RecipeCategory fromString(String inputValue) {
        return EnumUtils.fromString(RecipeCategory.class, inputValue);
    }
}
