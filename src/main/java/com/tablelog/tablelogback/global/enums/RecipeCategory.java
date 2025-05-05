package com.tablelog.tablelogback.global.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum RecipeCategory {
    밥요리,
    면요리,
    밑반찬,
    국or찌개,
    아침,
    점심,
    저녁,
    간식or후식,
    한식,
    양식,
    중식,
    일식,
    기타;

    @JsonCreator
    public static RecipeCategory fromString(String inputValue) {
        return EnumUtils.fromString(RecipeCategory.class, inputValue);
    }
}
