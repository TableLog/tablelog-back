package com.tablelog.tablelogback.global.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum PaymentMethod {
    Point,
    Card;

    @JsonCreator
    public static PaymentMethod fromString(String inputValue) {
        return EnumUtils.fromString(PaymentMethod.class, inputValue);
    }
}
