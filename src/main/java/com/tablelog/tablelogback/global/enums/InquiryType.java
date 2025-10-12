package com.tablelog.tablelogback.global.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum InquiryType {
    FOOD,
    DUPLICATE_USER,
    ETC;

    @JsonCreator
    public static InquiryType fromString(String inputValue) {
        return EnumUtils.fromString(InquiryType.class, inputValue);
    }
}
