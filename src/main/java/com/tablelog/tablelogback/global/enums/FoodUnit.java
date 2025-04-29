package com.tablelog.tablelogback.global.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum FoodUnit {
    kg,
    g,
    ml,
    L,
    cup,
    tbsp,
    개;

    @JsonCreator
    public static FoodUnit fromString(String inputValue) {
        return EnumUtils.fromString(FoodUnit.class, inputValue);
    }

    public double toBaseUnit(double amount) {
        // 모든 단위는 g 또는 ml 기준으로 통일한다고 가정
        switch (this) {
            case kg:
                return amount * 1000;  // kg → g
            case L:
                return amount * 1000;  // L → ml
            case cup:
                return amount * 240;   // 1 cup ≈ 240 ml
            case tbsp:
                return amount * 15;    // 1 tbsp ≈ 15 ml
            case g:
            case ml:
            case 개:
            default:
                return amount;
        }
    }
}
