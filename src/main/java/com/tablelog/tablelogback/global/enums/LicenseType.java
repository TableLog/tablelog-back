package com.tablelog.tablelogback.global.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum LicenseType {
    BUSINESS_REGISTRATION, // 사업자 등록증
    PATENT; // 특허

    @JsonCreator
    public static LicenseType fromString(String inputValue) {
        return EnumUtils.fromString(LicenseType.class, inputValue);
    }
}
